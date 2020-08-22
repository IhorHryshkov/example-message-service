package unit.com.example.ems.database.dao.redis;

import com.example.ems.database.dao.redis.StateDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StateDAOTest {
  @Mock private RedisTemplate<Object, Object> redisTemplate;
  @Mock private HashOperations<Object, Object, Object> hashOperations;
  @Mock private Map<String, DefaultRedisScript<Object>> luaScripts;
  @Mock private DefaultRedisScript<Object> defaultRedisScript;

  @InjectMocks private StateDAO stateDAO;

  @Test
  void add() {
    when(luaScripts.get(anyString())).thenReturn(defaultRedisScript);
    when(redisTemplate.execute(any(), anyList(), isA(Object.class), isA(Long.class)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(null, Collections.emptyList(), Collections.singletonList("data"));
    assertThat(catchThrowable(() -> stateDAO.add("test", "test", "test")))
        .as("Have some error")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(stateDAO.add("test", "test", "test"))
            .as("Result is null").isNull();
    assertThat(stateDAO.add("test", "test", "test"))
            .as("Result is empty").isNull();
    assertThat(stateDAO.add("test", "test", "test"))
            .as("Key is found").isEqualTo("data");

  }

  @Test
  void del() {
    when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    when(hashOperations.delete(any(Object.class), any(Object.class)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(0L, 3L);
    assertThat(catchThrowable(() -> stateDAO.del("test", "test")))
        .as("Have some error")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(stateDAO.del("test", "test"))
            .as("Result is less then or equal 0").isFalse();
    assertThat(stateDAO.del("test", "test"))
            .as("Result is more then 0").isTrue();
  }

  @Test
  void exist() {
    when(redisTemplate.opsForHash())
            .thenReturn(hashOperations);
    when(hashOperations.hasKey(anyString(), anyString()))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(null, false, true);
    assertThat(catchThrowable(() -> stateDAO.exist("test", "test")))
        .as("Check a key have error")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(stateDAO.exist("test", "test"))
            .as("Exist key is null").isNull();
    assertThat(stateDAO.exist("test", "test"))
            .as("Key is not found").isFalse();
    assertThat(stateDAO.exist("test", "test"))
            .as("Key is found").isTrue();
  }
}
