package io.github.hexagonframework.microservice.infra.uaa.client;

import java.util.Collections;
import org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Xuegui Yuan
 */
public class ServerTokenMvnEndpoint extends EndpointMvcAdapter {

  public ServerTokenMvnEndpoint(ServerTokenEndpoint delegate) {
    super(delegate);
  }

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  @Override
  public Object invoke() {
    if (!getDelegate().isEnabled()) {
      return new ResponseEntity<>(Collections.singletonMap(
          "message", "This endpoint is disabled"), HttpStatus.NOT_FOUND);
    }
    return super.invoke();
  }

  @Override
  public String getPath() {
    return "/server-token";
  }
}

