/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-11T20:48
 */
package com.example.ems.utils.converters.redis;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ReadingConverter
public class RedisReadingStringConverter implements Converter<String, Object> {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object convert(String source) {
    try {
      return objectMapper.readValue(source, Object.class);
    } catch (IOException e) {
      log.warn("Error while converting to Object.", e);
      throw new IllegalArgumentException("Can not convert to Object");
    }
  }

  @Override
  public JavaType getInputType(TypeFactory typeFactory) {
    return null;
  }

  @Override
  public JavaType getOutputType(TypeFactory typeFactory) {
    return null;
  }
}
