package unit.com.example.ems.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.example.ems.database.dao.redis.CacheDAO;
import com.example.ems.network.controllers.exceptions.global.ResponseIfNoneMatchException;
import com.example.ems.services.CacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CacheServiceTest {
  @Mock private CacheDAO cacheDAO;

  @InjectMocks private CacheService cacheService;

  @Test
  void existOrIfNoneMatch() {
    String key = "testKey";
    when(cacheDAO.exist(eq(key))).thenReturn(true, false);
    assertThat(catchThrowable(() -> cacheService.existOrIfNoneMatch(key)))
        .as("Key not found exception")
        .isInstanceOf(ResponseIfNoneMatchException.class);
    cacheService.existOrIfNoneMatch(key);
  }

  @Test
  void setKeyForCheckWithTtlDivider() {
    String key = "testKey";
    Integer divider = 2;
    doThrow(new RuntimeException("Test")).when(cacheDAO).setTtl(eq(key), eq(""), eq(divider));
    assertThat(catchThrowable(() -> cacheService.setKeyForCheckWithTtlDivider(key, 2)))
        .as("Some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");

    doNothing().when(cacheDAO).setTtl(eq(key), eq(""), eq(divider));
    cacheService.setKeyForCheckWithTtlDivider(key, 2);
  }

  @Test
  void hexistOrIfNoneMatch() {
    String key = "testKey";
    String hash = "testHash";
    when(cacheDAO.hexist(eq(key), eq(hash))).thenReturn(true, false);
    assertThat(catchThrowable(() -> cacheService.hexistOrIfNoneMatch(key, hash)))
        .as("Key not found exception")
        .isInstanceOf(ResponseIfNoneMatchException.class);
    cacheService.hexistOrIfNoneMatch(key, hash);
  }

  @Test
  void hset() {
    String key = "testKey";
    String hash = "testHash";
    String value = "testJSON";
    doThrow(new RuntimeException("Test")).when(cacheDAO).hset(eq(key), eq(hash), eq(value));
    assertThat(catchThrowable(() -> cacheService.hset(key, hash, value)))
        .as("Some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");

    doNothing().when(cacheDAO).hset(eq(key), eq(hash), eq(value));
    cacheService.hset(key, hash, value);
  }
}
