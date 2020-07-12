package com.example.ems.config.redis;

import com.example.ems.config.redis.factory.YamlPropertySourceFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.time.Duration;

@Data
@Slf4j
@Configuration
@EnableCaching
@ConfigurationProperties(prefix = "config")
@PropertySource(value = "classpath:redis.yml", factory = YamlPropertySourceFactory.class)
@EnableRedisRepositories(basePackages = "com.example.ems.database.dao.redis")
public class RedisConfig {

	private String password;
	private String host;
	private Integer port;
	private Long timeout;
	private Integer database;

	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
		redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
		redisStandaloneConfiguration.setDatabase(database);

		JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
		jedisClientConfiguration.connectTimeout(Duration.ofMillis(timeout));

		return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration.build());
	}

	@Bean
	public RedisTemplate<Object, Object> redisTemplate() {
		RedisTemplate<Object, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());
//		template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
//		template.setKeySerializer(new StringRedisSerializer());
//		template.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
//		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		return template;
	}

	@Bean
	public RedisCacheConfiguration cacheConfiguration() {
		RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofSeconds(600))
				.disableCachingNullValues();
		return cacheConfig;
	}

	@Bean
	public RedisCacheManager cacheManager() {
		RedisCacheManager rcm = RedisCacheManager.builder(jedisConnectionFactory())
				.cacheDefaults(cacheConfiguration())
				.transactionAware()
				.build();
		return rcm;
	}

//	@Bean
//	public RedisCustomConversions redisCustomConversions(RedisWritingStringConverter redisWritingStringConverter,
//	                                                     RedisReadingStringConverter redisReadingStringConverter) {
//		return new RedisCustomConversions(Arrays.asList(redisWritingStringConverter, redisReadingStringConverter));
//	}

}
