/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-11T20:52
 */
package com.example.ems.utils.converters.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@WritingConverter
public class RedisWritingStringConverter implements Converter<Object, String> {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convert(Object source) {
    try {
      return objectMapper.writeValueAsString(source);
    } catch (JsonProcessingException e) {
      log.warn("Error while converting Object to String.", e);
      throw new IllegalArgumentException("Can not convert Object to String");
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
