package com.example.ems.config.redis;

import com.example.ems.dto.database.redis.HashName;
import com.example.ems.dto.database.redis.LuaResPath;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "redis")
public class RedisSettings {
  private Integer cacheTtl;
  private HashName hashName;
  private LuaResPath luaResPath;
}
