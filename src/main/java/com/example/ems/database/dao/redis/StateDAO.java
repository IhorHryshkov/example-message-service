/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-08T14:50
 */
package com.example.ems.database.dao.redis;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

/** Component for work with redis DB state data */
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

  /**
   * Get value by key from hash state or add key with value to hash state use atomic transaction in
   * redis DB
   *
   * @param hashName Name of hash
   * @param key Key of hash
   * @param value Value of key
   * @return if hash name with key have in state than return value or if not than return null
   */
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

  /**
   * State delete key from hash and key from hash "expire" in redis DB
   *
   * @param hashName Name of hash
   * @param key Key of hash
   * @return return true if delete keys is successful or false if not
   */
  public boolean del(String hashName, String key) {
    Long resultHash = redisTemplate.opsForHash().delete(hashName, key);
    Long resultTime =
        redisTemplate.opsForHash().delete(String.format("%s::%s", hashName, "expire"), key);
    return resultHash > 0 && resultTime > 0;
  }

  /**
   * State check if exist this hash and key in redis DB
   *
   * @param hashName Name of hash for check
   * @param key Key of hash for check
   * @return return true if this hash and key exist or false if not exist
   */
  public Boolean exist(String hashName, String key) {
    return redisTemplate.opsForHash().hasKey(hashName, key);
  }
}
