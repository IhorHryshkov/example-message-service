package com.example.ems.config.redis;

import com.example.ems.config.redis.factory.YamlPropertySourceFactory;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Duration;

@Data
@Slf4j
@Configuration
@EnableCaching
@ConfigurationProperties(prefix = "config")
@PropertySource(value = "classpath:redis.yml", factory = YamlPropertySourceFactory.class)
@EnableTransactionManagement
public class RedisConfig {

	private String  password;
	private String  host;
	private Integer port;
	private Integer database;

	private final RedisSettings redisSettings;

	RedisConfig(RedisSettings redisSettings) {
		this.redisSettings = redisSettings;
	}


	@Bean(destroyMethod = "shutdown")
	public ClientResources clientResources() {
		return DefaultClientResources.create();
	}

	@Bean
	public RedisStandaloneConfiguration redisStandaloneConfiguration() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
		redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
		redisStandaloneConfiguration.setDatabase(database);
		return redisStandaloneConfiguration;
	}

	@Bean
	public ClientOptions clientOptions() {
		return ClientOptions.builder()
				.disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
				.autoReconnect(true)
				.build();
	}

	@Bean
	public LettucePoolingClientConfiguration lettucePoolConfig(
			ClientOptions options,
			@Qualifier("clientResources") ClientResources dcr
	) {
		return LettucePoolingClientConfiguration.builder()
				.poolConfig(new GenericObjectPoolConfig<>())
				.clientOptions(options)
				.clientResources(dcr)
				.build();
	}

	@Bean
	public RedisConnectionFactory connectionFactory(
			RedisStandaloneConfiguration redisStandaloneConfiguration,
			LettucePoolingClientConfiguration lettucePoolConfig
	) {
		return new LettuceConnectionFactory(redisStandaloneConfiguration, lettucePoolConfig);
	}

	@Bean
	@ConditionalOnMissingBean(name = "redisTemplate")
	@Primary
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<Object, Object> template = new RedisTemplate<>();
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

	@Bean
	public RedisCacheConfiguration cacheConfiguration() {
		log.debug("cacheConfiguration: {}", redisSettings.getCacheTtl());
		return RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofSeconds(redisSettings.getCacheTtl()))
				.disableCachingNullValues();
	}

	@Bean
	public RedisCacheManager cacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
		return RedisCacheManager.builder(lettuceConnectionFactory)
				.cacheDefaults(cacheConfiguration())
				.transactionAware()
				.build();
	}
}
