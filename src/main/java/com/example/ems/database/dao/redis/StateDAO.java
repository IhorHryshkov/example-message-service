/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-08T14:50
 */
package com.example.ems.database.dao.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class StateDAO {
  private final RedisTemplate<Object, Object> redisTemplate;
  private final Map<String, DefaultRedisScript<Object>> luaScripts;

  StateDAO(
      RedisTemplate<Object, Object> redisTemplate,
      Map<String, DefaultRedisScript<Object>> luaScripts) {
    this.redisTemplate = redisTemplate;
    this.luaScripts = luaScripts;
  }

  public Object add(String hashName, String key, Object value) {
    List result =
        (List)
            redisTemplate.execute(
                luaScripts.get("add"),
                Arrays.asList(hashName, key),
                value,
                Instant.now().toEpochMilli());
    log.debug("Add result: {}", result);
    return result == null || result.isEmpty() ? null : result.get(0);
  }

  public boolean del(String hashName, String key) {
    Long resultHash = redisTemplate.opsForHash().delete(hashName, key);
    Long resultTime =
        redisTemplate.opsForHash().delete(String.format("%s::%s", hashName, "expire"), key);
    return resultHash > 0 && resultTime > 0;
  }

  public Boolean exist(String hashName, String key) {
    return redisTemplate.opsForHash().hasKey(hashName, key);
  }
}
