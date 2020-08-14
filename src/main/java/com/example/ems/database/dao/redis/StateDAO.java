/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-08T14:50
 */
package com.example.ems.database.dao.redis;

import com.example.ems.config.redis.RedisSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class StateDAO {
	private final RedisTemplate<Object, Object> redisTemplate;
	private final RedisSettings redisSettings;

	private DefaultRedisScript<Object> addLua;

	StateDAO(RedisTemplate<Object, Object> redisTemplate, RedisSettings redisSettings) {
		this.redisTemplate = redisTemplate;
		this.redisSettings = redisSettings;
	}

	@PostConstruct
	private void init() {
		addLua = new DefaultRedisScript<>();
		addLua.setScriptSource(new ResourceScriptSource(new ClassPathResource(redisSettings.getLuaResPath().getAdd())));
		addLua.setResultType(Object.class);
	}

	public Object add(String hashName, String key, Object value) {
		List result = (List) redisTemplate.execute(addLua, Arrays.asList(hashName, key), value, Instant.now().toEpochMilli());
		log.debug("Add result: {}", result);
		return result == null || result.isEmpty() ? null : result.get(0);
	}

	public boolean del(String hashName, String key) {
		Long resultHash = redisTemplate.opsForHash().delete(hashName, key);
		Long resultTime = redisTemplate.opsForHash().delete(String.format("%s::%s", hashName, "expire"), key);
		return resultHash > 0 && resultTime > 0;
	}

	public boolean exist(String hashName, String key) {
		Object result = redisTemplate.opsForHash().get(hashName, key);
		return result != null;
	}

}
