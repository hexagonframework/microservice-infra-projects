package io.github.hexagonframework.microservice.infra.gateway.errorlimit;

import io.github.hexagonframework.microservice.infra.gateway.errorlimit.config.ErrorLimitPolicy;
import io.github.hexagonframework.microservice.infra.gateway.errorlimit.config.ErrorLimitProperties;
import io.github.hexagonframework.microservice.infra.gateway.errorlimit.config.ErrorPolicyKeyValue;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.AllArgsConstructor;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Xuegui Yuan
 */
@AllArgsConstructor
public abstract class AbstractErrorLimitFilter extends ZuulFilter {

    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();
    private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";
    private static final String X_DEVICE_ID = "X-DEVICE-ID";
    private static final String ANONYMOUS = "anonymous";
    protected final ErrorLimitProperties properties;
    protected final RouteLocator routeLocator;

    /**
     * Get the requestURI from request.
     *
     * @return The request URI
     */
    protected String requestURI() {
        return URL_PATH_HELPER.getPathWithinApplication(RequestContext.getCurrentContext().getRequest());
    }

    /**
     * Return the requestedContext from request.
     *
     * @return The requestedContext
     */
    protected Route route() {
        return this.routeLocator.getMatchingRoute(this.requestURI());
    }

    protected ErrorPolicyKeyValue getFullPathPolicy(Route route) {
        if (route == null) {
            return null;
        }
        ErrorLimitPolicy errorLimitPolicy = this.properties.getPolicies().get(route.getFullPath());
        return errorLimitPolicy != null ? new ErrorPolicyKeyValue(route.getFullPath(), errorLimitPolicy) : null;
    }

    protected ErrorPolicyKeyValue getRouteIdPolicy(Route route) {
        if (route == null) {
            return null;
        }
        ErrorLimitPolicy errorLimitPolicy = this.properties.getPolicies().get(route.getId());
        return (errorLimitPolicy != null) ? new ErrorPolicyKeyValue(route.getId(), errorLimitPolicy) : null;
    }

    protected ErrorPolicyKeyValue policy() {
        Route route = route();
        ErrorPolicyKeyValue routeFullPathPolicy = getFullPathPolicy(route);
        ErrorPolicyKeyValue routeIdPolicy = getRouteIdPolicy(route);
        if (routeFullPathPolicy != null) {
            return routeFullPathPolicy;
        }
        if (routeIdPolicy != null) {
            return routeIdPolicy;
        }
        return null;
    }

    protected String key(final HttpServletRequest request, final String key, final List<ErrorLimitPolicy.Type> types) {
        final Route route = route();
        final StringBuilder builder = new StringBuilder(key);
        if (types.contains(ErrorLimitPolicy.Type.ORIGIN)) {
            builder.append(":").append(getRemoteAddr(request));
        }
        if (types.contains(ErrorLimitPolicy.Type.USER)) {
            builder.append(":").append((request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() :
                    ANONYMOUS);
        }
        if (types.contains(ErrorLimitPolicy.Type.DEVICE)) {
            builder.append(":").append(getDevivceId(request));
        }
        return builder.toString();
    }

    protected String getRemoteAddr(final HttpServletRequest request) {
        final String remoteAddr;
        if (this.properties.isBehindProxy() && request.getHeader(X_FORWARDED_FOR) != null) {
            remoteAddr = request.getHeader(X_FORWARDED_FOR);
        } else {
            remoteAddr = request.getRemoteAddr();
        }
        return remoteAddr;
    }

    private String getDevivceId(final HttpServletRequest request) {
        final String remoteAddr;
        if (this.properties.isBehindProxy() && request.getHeader(X_DEVICE_ID) != null) {
            remoteAddr = request.getHeader(X_DEVICE_ID);
        } else {
            remoteAddr = request.getRemoteAddr();
        }
        return remoteAddr;
    }

    protected interface Headers {
        String LIMIT = "X-RateLimit-Limit";
        String REMAINING = "X-RateLimit-Remaining";
        String RESET = "X-RateLimit-Reset";
    }
}
