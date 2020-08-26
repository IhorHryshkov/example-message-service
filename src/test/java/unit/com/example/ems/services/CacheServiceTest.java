/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-13T09:35
 */
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
    String keyExpected = "testKey";
    when(cacheDAO.exist(eq(keyExpected))).thenReturn(true, false);
    assertThat(catchThrowable(() -> cacheService.existOrIfNoneMatch(key)))
        .as("Key not found exception")
        .isInstanceOf(ResponseIfNoneMatchException.class);
    cacheService.existOrIfNoneMatch(key);
  }

  @Test
  void setKeyForCheckWithTtlDivider() {
    String key = "testKey";
    Integer divider = 2;
    String keyExpected = "testKey";
    Integer dividerExpected = 2;
    doThrow(new RuntimeException("Test"))
        .when(cacheDAO)
        .setTtl(eq(keyExpected), eq(""), eq(dividerExpected));
    assertThat(catchThrowable(() -> cacheService.setKeyForCheckWithTtlDivider(key, divider)))
        .as("Some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");

    doNothing().when(cacheDAO).setTtl(eq(keyExpected), eq(""), eq(dividerExpected));
    cacheService.setKeyForCheckWithTtlDivider(key, divider);
  }

  @Test
  void hexistOrIfNoneMatch() {
    String key = "testKey";
    String hash = "testHash";
    String keyExpected = "testKey";
    String hashExpected = "testHash";
    when(cacheDAO.hexist(eq(keyExpected), eq(hashExpected))).thenReturn(true, false);
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
    String keyExpected = "testKey";
    String hashExpected = "testHash";
    String valueExpected = "testJSON";
    doThrow(new RuntimeException("Test"))
        .when(cacheDAO)
        .hset(eq(keyExpected), eq(hashExpected), eq(valueExpected));
    assertThat(catchThrowable(() -> cacheService.hset(key, hash, value)))
        .as("Some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");

    doNothing().when(cacheDAO).hset(eq(keyExpected), eq(hashExpected), eq(valueExpected));
    cacheService.hset(key, hash, value);
  }
}
