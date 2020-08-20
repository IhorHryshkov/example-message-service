package unit.com.example.ems.config.redis;

import com.example.ems.config.redis.RedisConfig;
import com.example.ems.config.redis.RedisSettings;
import com.greghaskins.spectrum.Spectrum;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Rule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.time.Duration;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@RunWith(Spectrum.class)
class RedisConfigTest {
	@Mock
	private RedisSettings                     redisSettings;
	@Mock
	private ClientOptions                     options;
	@Mock
	private ClientResources                   dcr;
	@Mock
	private RedisStandaloneConfiguration      redisStandaloneConfiguration;
	@Mock
	private LettucePoolingClientConfiguration lettucePoolConfig;
	@Mock
	private RedisConnectionFactory            redisConnectionFactory;
	@Mock
	private LettuceConnectionFactory          lettuceConnectionFactory;

	@InjectMocks
	private RedisConfig redisConfig;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	{
		describe("RedisConfig 'clientResources' method test", () -> {
			it(
					"Should return is not null value",
					() -> assertThat(redisConfig.clientResources()).as("ClientResources is not null").isNotNull()
			);
			it(
					"Should return is DefaultClientResources class",
					() -> assertThat(redisConfig.clientResources())
							.as("ClientResource classname")
							.isOfAnyClassIn(DefaultClientResources.class)
			);
		});
		describe("RedisConfig 'redisStandaloneConfiguration' method test", () -> {
			beforeEach(() -> {
				redisConfig.setDatabase(1);
				redisConfig.setHost("http://testHost");
				redisConfig.setPassword("testPass");
				redisConfig.setPort(80);
			});
			it(
					"Should return is not null value",
					() -> assertThat(redisConfig.redisStandaloneConfiguration())
							.as("RedisStandaloneConfiguration is not null")
							.isNotNull()
			);
			it(
					"Should return is RedisStandaloneConfiguration class",
					() -> assertThat(redisConfig.redisStandaloneConfiguration())
							.as("RedisStandaloneConfiguration classname")
							.isOfAnyClassIn(RedisStandaloneConfiguration.class)
			);
			it(
					"Should return new RedisStandaloneConfiguration object with init data",
					() -> {
						RedisStandaloneConfiguration redisStandaloneConfiguration = redisConfig.redisStandaloneConfiguration();

						assertThat(redisStandaloneConfiguration.getDatabase())
								.as("Redis database number")
								.isEqualTo(redisConfig.getDatabase());
						assertThat(redisStandaloneConfiguration.getHostName())
								.as("Redis hostname")
								.isEqualTo(redisConfig.getHost());
						assertThat(redisStandaloneConfiguration.getPort())
								.as("Redis port number")
								.isEqualTo(redisConfig.getPort());
						assertThat(redisStandaloneConfiguration.getPassword().get())
								.as("Redis password")
								.isEqualTo(RedisPassword.of(redisConfig.getPassword()).get());
					}
			);
		});
		describe("RedisConfig 'clientOptions' method test", () -> {
			it(
					"Should return is not null value",
					() -> assertThat(redisConfig.clientOptions()).as("ClientOptions is not null").isNotNull()
			);
			it(
					"Should return is ClientOptions class",
					() -> assertThat(redisConfig.clientOptions())
							.as("ClientOptions classname")
							.isOfAnyClassIn(ClientOptions.class)
			);
			it(
					"Should return new ClientOptions object with init data",
					() -> {
						ClientOptions clientOptions = redisConfig.clientOptions();
						assertThat(clientOptions.getDisconnectedBehavior())
								.as("Disconnected behavior")
								.isEqualByComparingTo(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS);
						assertThat(clientOptions.isAutoReconnect()).as("Auto reconnect").isTrue();
					}
			);
		});
		describe("RedisConfig 'lettucePoolConfig' method test", () -> {
			it(
					"Should return is not null value",
					() -> assertThat(redisConfig.lettucePoolConfig(options, dcr))
							.as("LettucePoolingClientConfiguration is not null")
							.isNotNull()
			);
			it(
					"Should return is LettucePoolingClientConfiguration class",
					() -> assertThat(redisConfig.lettucePoolConfig(options, dcr).getClass().getInterfaces())
							.as("LettucePoolingClientConfiguration classname")
							.contains(LettucePoolingClientConfiguration.class)
			);
			it(
					"Should return is GenericObjectPoolConfig class for pool config",
					() -> assertThat((redisConfig.lettucePoolConfig(options, dcr)).getPoolConfig())
							.as("GenericObjectPoolConfig classname")
							.isOfAnyClassIn(GenericObjectPoolConfig.class)
			);
		});
		describe("RedisConfig 'connectionFactory' method test", () -> {
			it(
					"Should return is not null value",
					() -> assertThat(redisConfig.connectionFactory(redisStandaloneConfiguration, lettucePoolConfig))
							.as("LettucePoolingClientConfiguration is not null")
							.isNotNull()
			);
			it(
					"Should return is RedisConnectionFactory class",
					() -> assertThat(redisConfig
							                 .connectionFactory(redisStandaloneConfiguration, lettucePoolConfig)
							                 .getClass()
							                 .getInterfaces())
							.as("RedisConnectionFactory classname")
							.contains(RedisConnectionFactory.class)
			);
		});
		describe("RedisConfig 'redisTemplate' method test", () -> {
			it(
					"Should return is not null value",
					() -> assertThat(redisConfig.redisTemplate(redisConnectionFactory))
							.as("RedisTemplate is not null")
							.isNotNull()
			);
			it(
					"Should return is RedisTemplate class",
					() -> assertThat(redisConfig.redisTemplate(redisConnectionFactory))
							.as("RedisTemplate classname")
							.isOfAnyClassIn(RedisTemplate.class)
			);
			it(
					"Should return new RedisTemplate object with init data",
					() -> {
						RedisTemplate<Object, Object> redisTemplate = redisConfig.redisTemplate(redisConnectionFactory);
						assertThat(redisTemplate.getKeySerializer())
								.as("KeySerializer classname")
								.isOfAnyClassIn(StringRedisSerializer.class);
						assertThat(redisTemplate.getHashKeySerializer())
								.as("HashKeySerializer classname")
								.isOfAnyClassIn(StringRedisSerializer.class);
						assertThat(redisTemplate.getValueSerializer())
								.as("ValueSerializer classname")
								.isOfAnyClassIn(GenericJackson2JsonRedisSerializer.class);
						assertThat(redisTemplate.getHashValueSerializer())
								.as("HashValueSerializer classname")
								.isOfAnyClassIn(GenericJackson2JsonRedisSerializer.class);
						assertThat(redisTemplate.getConnectionFactory())
								.as("ConnectionFactory object")
								.isEqualTo(redisConnectionFactory);
					}
			);

		});
		describe("RedisConfig 'cacheConfiguration' method test", () -> {
			beforeEach(() -> {
				redisSettings.setCacheTtl(100);
			});
			it(
					"Should return is not null value",
					() -> assertThat(redisConfig.cacheConfiguration())
							.as("RedisCacheConfiguration is not null")
							.isNotNull()
			);
			it(
					"Should return is RedisCacheConfiguration class",
					() -> assertThat(redisConfig.cacheConfiguration())
							.as("RedisCacheConfiguration classname")
							.isOfAnyClassIn(RedisCacheConfiguration.class)
			);
			it(
					"Should return new RedisCacheConfiguration object with init data",
					() -> {
						RedisCacheConfiguration redisCacheConfiguration = redisConfig.cacheConfiguration();
						assertThat(redisCacheConfiguration.getTtl())
								.as("Cache TTL")
								.isEqualTo(Duration.ofSeconds(redisSettings.getCacheTtl()));
						assertThat(redisCacheConfiguration.getAllowCacheNullValues())
								.as("Allow cache null values")
								.isFalse();
					}
			);
		});
		describe("RedisConfig 'cacheManager' method test", () -> {
			beforeEach(() -> {
				redisSettings.setCacheTtl(100);
			});
			it(
					"Should return is not null value",
					() -> assertThat(redisConfig.cacheManager(lettuceConnectionFactory))
							.as("RedisCacheManager is not null")
							.isNotNull()
			);
			it(
					"Should return is RedisCacheManager class",
					() -> assertThat(redisConfig.cacheManager(lettuceConnectionFactory))
							.as("RedisCacheManager classname")
							.isOfAnyClassIn(RedisCacheManager.class)
			);
			it(
					"Should return transaction aware is true",
					() -> {
						RedisCacheManager redisCacheManager = redisConfig.cacheManager(lettuceConnectionFactory);
						assertThat(redisCacheManager.isTransactionAware())
								.as("Transaction aware")
								.isTrue();
					}
			);
		});
	}
}
