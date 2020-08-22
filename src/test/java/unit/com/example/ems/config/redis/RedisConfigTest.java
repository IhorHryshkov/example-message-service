package unit.com.example.ems.config.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.ems.config.redis.RedisConfig;
import com.example.ems.config.redis.RedisSettings;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import java.time.Duration;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

@ExtendWith(MockitoExtension.class)
class RedisConfigTest {
  @Mock private RedisSettings redisSettings;
  @Mock private ClientOptions options;
  @Mock private ClientResources dcr;
  @Mock private RedisStandaloneConfiguration redisStandaloneConfiguration;
  @Mock private LettucePoolingClientConfiguration lettucePoolConfig;
  @Mock private RedisConnectionFactory redisConnectionFactory;
  @Mock private LettuceConnectionFactory lettuceConnectionFactory;

  @InjectMocks private RedisConfig redisConfig;

  @BeforeEach
  void setUp() {
    redisConfig.setDatabase(1);
    redisConfig.setHost("http://testHost");
    redisConfig.setPassword("testPass");
    redisConfig.setPort(80);
  }

  @Test
  void clientResources() {
    assertThat(redisConfig.clientResources()).as("ClientResources is not null").isNotNull();
    assertThat(redisConfig.clientResources())
        .as("Class is not ClientResource")
        .isOfAnyClassIn(DefaultClientResources.class);
  }

  @Test
  void redisStandaloneConfiguration() {
    RedisStandaloneConfiguration redisStandaloneConfiguration =
        redisConfig.redisStandaloneConfiguration();
    assertThat(redisStandaloneConfiguration)
        .as("RedisStandaloneConfiguration is not null")
        .isNotNull();
    assertThat(redisStandaloneConfiguration)
        .as("Class is not RedisStandaloneConfiguration")
        .isOfAnyClassIn(RedisStandaloneConfiguration.class);
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

  @Test
  void clientOptions() {
    ClientOptions clientOptions = redisConfig.clientOptions();
    assertThat(clientOptions).as("ClientOptions is not null").isNotNull();
    assertThat(clientOptions).as("Class is not ClientOptions").isOfAnyClassIn(ClientOptions.class);
    assertThat(clientOptions.getDisconnectedBehavior())
        .as("Disconnected behavior")
        .isEqualByComparingTo(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS);
    assertThat(clientOptions.isAutoReconnect()).as("Auto reconnect").isTrue();
  }

  @Test
  void lettucePoolConfig() {
    assertThat(redisConfig.lettucePoolConfig(options, dcr))
        .as("LettucePoolingClientConfiguration is not null")
        .isNotNull();
    assertThat(redisConfig.lettucePoolConfig(options, dcr).getClass().getInterfaces())
        .as("Class is not LettucePoolingClientConfiguration")
        .contains(LettucePoolingClientConfiguration.class);
    assertThat((redisConfig.lettucePoolConfig(options, dcr)).getPoolConfig())
        .as("Class is not GenericObjectPoolConfig")
        .isOfAnyClassIn(GenericObjectPoolConfig.class);
  }

  @Test
  void connectionFactory() {
    assertThat(redisConfig.connectionFactory(redisStandaloneConfiguration, lettucePoolConfig))
        .as("LettucePoolingClientConfiguration is not null")
        .isNotNull();
    assertThat(
            redisConfig
                .connectionFactory(redisStandaloneConfiguration, lettucePoolConfig)
                .getClass()
                .getInterfaces())
        .as("Class is not RedisConnectionFactory")
        .contains(RedisConnectionFactory.class);
  }

  @Test
  void redisTemplate() {
    RedisTemplate<Object, Object> redisTemplate = redisConfig.redisTemplate(redisConnectionFactory);
    assertThat(redisTemplate).as("RedisTemplate is not null").isNotNull();
    assertThat(redisTemplate).as("Class is not RedisTemplate").isOfAnyClassIn(RedisTemplate.class);
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

  @Test
  void cacheConfiguration() {
    when(redisSettings.getCacheTtl()).thenReturn(100);
    RedisCacheConfiguration redisCacheConfiguration = redisConfig.cacheConfiguration();
    assertThat(redisCacheConfiguration).as("RedisCacheConfiguration is not null").isNotNull();
    assertThat(redisCacheConfiguration)
        .as("Class is not RedisCacheConfiguration")
        .isOfAnyClassIn(RedisCacheConfiguration.class);
    assertThat(redisCacheConfiguration.getTtl()).as("Cache TTL").isEqualTo(Duration.ofSeconds(100));
    assertThat(redisCacheConfiguration.getAllowCacheNullValues())
        .as("Allow cache null values")
        .isFalse();
  }

  @Test
  void cacheManager() {
    RedisCacheManager redisCacheManager = redisConfig.cacheManager(lettuceConnectionFactory);
    assertThat(redisCacheManager).as("RedisCacheManager is not null").isNotNull();
    assertThat(redisCacheManager)
        .as("Class is not RedisCacheManager")
        .isOfAnyClassIn(RedisCacheManager.class);
    assertThat(redisCacheManager.isTransactionAware()).as("Transaction aware").isTrue();
  }
}
