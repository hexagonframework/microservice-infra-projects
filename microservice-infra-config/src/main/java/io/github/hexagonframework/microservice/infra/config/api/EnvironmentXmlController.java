package io.github.hexagonframework.microservice.infra.config.api;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.cloud.config.server.environment.RepositoryException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.springframework.cloud.config.server.support.EnvironmentPropertySource.prepareEnvironment;
import static org.springframework.cloud.config.server.support.EnvironmentPropertySource.resolvePlaceholders;

/**
 * 提供以XML格式返回配置的HTTP接口
 * @author Xuegui Yuan
 */
@RestController
@RequestMapping(method = RequestMethod.GET, path = "${spring.cloud.config.server.prefix:}")
public class EnvironmentXmlController {
    @Autowired
    private EnvironmentRepository repository;

    private XmlMapper xmlMapper = new XmlMapper();


    public Environment labelled(String name, String profiles, String label) {
        if (label != null && label.contains("(_)")) {
            // "(_)" is uncommon in a git branch name, but "/" cannot be matched
            // by Spring MVC
            label = label.replace("(_)", "/");
        }
        Environment environment = this.repository.findOne(name, profiles, label);
        return environment;
    }

    @RequestMapping("{name}-{profiles}.xml")
    public ResponseEntity<String> xmlProperties(@PathVariable String name,
                                                 @PathVariable String profiles,
                                                 @RequestParam(defaultValue = "true") boolean resolvePlaceholders)
            throws Exception {
        return labelledXmlProperties(name, profiles, null, resolvePlaceholders);
    }

    @RequestMapping("/{label}/{name}-{profiles}.xml")
    public ResponseEntity<String> labelledXmlProperties(@PathVariable String name,
                                                         @PathVariable String profiles, @PathVariable String label,
                                                         @RequestParam(defaultValue = "true") boolean resolvePlaceholders)
            throws Exception {
        validateProfiles(profiles);
        Environment environment = labelled(name, profiles, label);
        Map<String, Object> properties = convertToMap(environment);
        String json = this.xmlMapper.writeValueAsString(properties);
        if (resolvePlaceholders) {
            json = resolvePlaceholders(prepareEnvironment(environment), json);
        }
        return getSuccess(json, MediaType.APPLICATION_XML);
    }

    /**
     * Method {@code convertToMap} converts an {@code Environment} to a nested Map which represents a yml/json structure.
     *
     * @param input the environment to be converted
     * @return the nested map containing the environment's properties
     */
    private Map<String, Object> convertToMap(Environment input) {
        // First use the current convertToProperties to get a flat Map from the environment
        Map<String, Object> properties = convertToProperties(input);

        // The root map which holds all the first level properties
        Map<String, Object> rootMap = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            PropertyNavigator nav = new PropertyNavigator(key);
            nav.setMapValue(rootMap, value);
        }
        return rootMap;
    }

    @ExceptionHandler(RepositoryException.class)
    public void noSuchLabel(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void illegalArgument(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    private void validateProfiles(String profiles) {
        if (profiles.contains("-")) {
            throw new IllegalArgumentException(
                    "Properties output not supported for name or profiles containing hyphens");
        }
    }

    private HttpHeaders getHttpHeaders(MediaType mediaType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(mediaType);
        return httpHeaders;
    }

    private ResponseEntity<String> getSuccess(String body) {
        return new ResponseEntity<>(body, getHttpHeaders(MediaType.TEXT_PLAIN),
                HttpStatus.OK);
    }

    private ResponseEntity<String> getSuccess(String body, MediaType mediaType) {
        return new ResponseEntity<>(body, getHttpHeaders(mediaType), HttpStatus.OK);
    }

    private Map<String, Object> convertToProperties(Environment profiles) {

        // Map of unique keys containing full map of properties for each unique
        // key
        Map<String, Map<String, Object>> map = new LinkedHashMap<>();
        List<PropertySource> sources = new ArrayList<>(profiles.getPropertySources());
        Collections.reverse(sources);
        Map<String, Object> combinedMap = new TreeMap<>();
        for (PropertySource source : sources) {

            @SuppressWarnings("unchecked")
            Map<String, Object> value = (Map<String, Object>) source.getSource();
            for (String key : value.keySet()) {

                if (!key.contains("[")) {

                    // Not an array, add unique key to the map
                    combinedMap.put(key, value.get(key));

                }
                else {

                    // An existing array might have already been added to the property map
                    // of an unequal size to the current array. Replace the array key in
                    // the current map.
                    key = key.substring(0, key.indexOf("["));
                    Map<String, Object> filtered = new TreeMap<>();
                    for (String index : value.keySet()) {
                        if (index.startsWith(key + "[")) {
                            filtered.put(index, value.get(index));
                        }
                    }
                    map.put(key, filtered);
                }
            }

        }

        // Combine all unique keys for array values into the combined map
        for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
            combinedMap.putAll(entry.getValue());
        }

        postProcessProperties(combinedMap);
        return combinedMap;
    }

    private void postProcessProperties(Map<String, Object> propertiesMap) {
        for (Iterator<String> iter = propertiesMap.keySet().iterator(); iter.hasNext();) {
            String key = iter.next();
            if (key.equals("spring.profiles")) {
                iter.remove();
            }
        }
    }

    /**
     * Class {@code PropertyNavigator} is used to navigate through the property key and create necessary Maps and Lists
     * making up the nested structure to finally set the property value at the leaf node.
     * <p>
     * The following rules in yml/json are implemented:
     * <pre>
     * 1. an array element can be:
     *    - a value (leaf)
     *    - a map
     *    - a nested array
     * 2. a map value can be:
     *    - a value (leaf)
     *    - a nested map
     *    - an array
     * </pre>
     */
    private static class PropertyNavigator {

        private enum NodeType {LEAF, MAP, ARRAY}

        private final String propertyKey;
        private int currentPos;
        private NodeType valueType;

        private PropertyNavigator(String propertyKey) {
            this.propertyKey = propertyKey;
            currentPos = -1;
            valueType = NodeType.MAP;
        }

        private void setMapValue(Map<String, Object> map, Object value) {
            String key = getKey();
            if (NodeType.MAP.equals(valueType)) {
                Map<String, Object> nestedMap = (Map<String, Object>) map.get(key);
                if (nestedMap == null) {
                    nestedMap = new LinkedHashMap<>();
                    map.put(key, nestedMap);
                }
                setMapValue(nestedMap, value);
            } else if (NodeType.ARRAY.equals(valueType)) {
                List<Object> list = (List<Object>) map.get(key);
                if (list == null) {
                    list = new ArrayList<>();
                    map.put(key, list);
                }
                setListValue(list, value);
            } else {
                map.put(key, value);
            }
        }

        private void setListValue(List<Object> list, Object value) {
            int index = getIndex();
            // Fill missing elements if needed
            while (list.size() <= index) {
                list.add(null);
            }
            if (NodeType.MAP.equals(valueType)) {
                Map<String, Object> map = (Map<String, Object>) list.get(index);
                if (map == null) {
                    map = new LinkedHashMap<>();
                    list.set(index, map);
                }
                setMapValue(map, value);
            } else if (NodeType.ARRAY.equals(valueType)) {
                List<Object> nestedList = (List<Object>) list.get(index);
                if (nestedList == null) {
                    nestedList = new ArrayList<>();
                    list.set(index, nestedList);
                }
                setListValue(nestedList, value);
            } else {
                list.set(index, value);
            }
        }

        private int getIndex() {
            // Consider [
            int start = currentPos + 1;

            for (int i = start; i < propertyKey.length(); i++) {
                char c = propertyKey.charAt(i);
                if (c == ']') {
                    currentPos = i;
                    break;
                } else if (!Character.isDigit(c)) {
                    throw new IllegalArgumentException("Invalid key: " + propertyKey);
                }
            }
            // If no closing ] or if '[]'
            if (currentPos < start || currentPos == start) {
                throw new IllegalArgumentException("Invalid key: " + propertyKey);
            } else {
                int index = Integer.parseInt(propertyKey.substring(start, currentPos));
                // Skip the closing ]
                currentPos++;
                if (currentPos == propertyKey.length()) {
                    valueType = NodeType.LEAF;
                } else {
                    switch (propertyKey.charAt(currentPos)) {
                        case '.':
                            valueType = NodeType.MAP;
                            break;
                        case '[':
                            valueType = NodeType.ARRAY;
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid key: " + propertyKey);
                    }
                }
                return index;
            }
        }

        private String getKey() {
            // Consider initial value or previous char '.' or '['
            int start = currentPos + 1;
            for (int i = start; i < propertyKey.length(); i++) {
                char currentChar = propertyKey.charAt(i);
                if (currentChar == '.') {
                    valueType = NodeType.MAP;
                    currentPos = i;
                    break;
                } else if (currentChar == '[') {
                    valueType = NodeType.ARRAY;
                    currentPos = i;
                    break;
                }
            }
            // If there's no delimiter then it's a key of a leaf
            if (currentPos < start) {
                currentPos = propertyKey.length();
                valueType = NodeType.LEAF;
                // Else if we encounter '..' or '.[' or start of the property is . or [ then it's invalid
            } else if (currentPos == start) {
                throw new IllegalArgumentException("Invalid key: " + propertyKey);
            }
            return propertyKey.substring(start, currentPos);
        }
    }
}
