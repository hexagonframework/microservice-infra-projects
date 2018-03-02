package io.github.hexagonframework.microservice.infra.uaa.client;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

/**
 * 自定义InvalidTokenException JSON序列化.
 *
 * @author Xuegui Yuan
 */
public class CustomInvalidTokenExceptionSerializer extends StdSerializer<CustomInvalidTokenException> {

  public CustomInvalidTokenExceptionSerializer() {
    super(CustomInvalidTokenException.class);
  }

  @Override
  public void serialize(CustomInvalidTokenException e, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeStringField("errCode", "401");
    jsonGenerator.writeStringField("errMessage", "非法访问令牌，请重新登录");
    jsonGenerator.writeEndObject();
  }
}
