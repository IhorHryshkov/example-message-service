package unit.com.example.ems.database.dao.redis;

import com.example.ems.config.redis.RedisSettings;
import com.example.ems.database.dao.redis.CacheDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CacheDAOTest {
  @Mock private HashOperations<Object, Object, Object> hashOperations;
  @Mock private BoundValueOperations<Object, Object> boundValueOperations;
  @Mock private RedisTemplate<Object, Object> redisTemplate;
  @Mock private RedisSettings redisSettings;

  @InjectMocks private CacheDAO cacheDAO;

  @Test
  void exist() {
    when(redisTemplate.hasKey(anyString()))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(null, false, true);
    assertThat(catchThrowable(() -> cacheDAO.exist("test")))
        .as("Check a key have error")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(cacheDAO.exist("test")).as("Exist key is null").isNull();
    assertThat(cacheDAO.exist("test")).as("Key is not found").isFalse();
    assertThat(cacheDAO.exist("test")).as("Key is found").isTrue();
  }

  @Test
  void hexist() {
    when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    when(hashOperations.hasKey(anyString(), anyString()))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(null, false, true);
    assertThat(catchThrowable(() -> cacheDAO.hexist("test", "test")))
        .as("Check a key have error")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(cacheDAO.hexist("test", null)).as("Check hash of null").isFalse();
    assertThat(cacheDAO.hexist("test", "test")).as("Exist key is null").isNull();
    assertThat(cacheDAO.hexist("test", "test")).as("Key is not found").isFalse();
    assertThat(cacheDAO.hexist("test", "test")).as("Key is found").isTrue();
  }

  @Test
  void setTtl() {
    when(redisSettings.getCacheTtl()).thenReturn(100);
    when(redisTemplate.boundValueOps(isNull())).thenReturn(null);
    when(redisTemplate.boundValueOps(anyString())).thenReturn(boundValueOperations);
    assertThat(catchThrowable(() -> cacheDAO.setTtl(null, "test", 2)))
        .as("Key value is null")
        .isInstanceOf(NullPointerException.class);
    assertThat(catchThrowable(() -> cacheDAO.setTtl("test", null, 0)))
        .as("Divider value is incorrect")
        .isInstanceOf(ArithmeticException.class);
    ArgumentCaptor<Duration> durationCapture = ArgumentCaptor.forClass(Duration.class);
    ArgumentCaptor<Object> objectCapture = ArgumentCaptor.forClass(Object.class);
    ArgumentCaptor<String> keyCapture = ArgumentCaptor.forClass(String.class);
    when(redisTemplate.boundValueOps(keyCapture.capture())).thenReturn(boundValueOperations);
    doNothing().when(boundValueOperations).set(objectCapture.capture(), durationCapture.capture());
    cacheDAO.setTtl("key", "test", 2);
    assertThat(keyCapture.getValue()).as("Key value is correct").isEqualTo("key");
    assertThat(objectCapture.getValue()).as("Data value is correct").isEqualTo("test");
    assertThat(durationCapture.getValue())
        .as("Time value is correct")
        .isEqualTo(Duration.ofSeconds(50));
  }

  @Test
  void set() {
    when(redisTemplate.boundValueOps(isNull())).thenReturn(null);
    assertThat(catchThrowable(() -> cacheDAO.set(null, "test")))
        .as("Key value is null")
        .isInstanceOf(NullPointerException.class);
    ArgumentCaptor<Object> objectCapture = ArgumentCaptor.forClass(Object.class);
    ArgumentCaptor<String> keyCapture = ArgumentCaptor.forClass(String.class);
    when(redisTemplate.boundValueOps(keyCapture.capture())).thenReturn(boundValueOperations);
    doNothing().when(boundValueOperations).set(objectCapture.capture());
    cacheDAO.set("key", "test");
    assertThat(keyCapture.getValue()).as("Key value is correct").isEqualTo("key");
    assertThat(objectCapture.getValue()).as("Data value is correct").isEqualTo("test");
  }

  @Test
  void hset() {
    when(redisTemplate.opsForHash())
            .thenReturn(hashOperations);
    ArgumentCaptor<String> hashCapture = ArgumentCaptor
            .forClass(String.class);
    ArgumentCaptor<String> valueCapture = ArgumentCaptor
            .forClass(String.class);
    ArgumentCaptor<String> keyCapture = ArgumentCaptor
            .forClass(String.class);
    doNothing().when(hashOperations).put(keyCapture.capture(), hashCapture.capture(), valueCapture.capture());
    cacheDAO.hset("key", "test", "data");
    assertThat(keyCapture.getValue()).as("Key value is correct").isEqualTo("key");
    assertThat(hashCapture.getValue()).as("Hash value is correct").isEqualTo("test");
    assertThat(valueCapture.getValue()).as("Data is correct").isEqualTo("data");
  }
}
