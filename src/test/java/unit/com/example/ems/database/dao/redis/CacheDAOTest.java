package unit.com.example.ems.database.dao.redis;

import com.example.ems.config.redis.RedisSettings;
import com.example.ems.database.dao.redis.CacheDAO;
import com.greghaskins.spectrum.Spectrum;
import org.junit.Rule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(Spectrum.class)
class CacheDAOTest {
	@Mock
	private HashOperations<Object, Object, Object> hashOperations;
	@Mock
	private RedisTemplate<Object, Object>          redisTemplate;
	@Mock
	private RedisSettings                          redisSettings;

	@InjectMocks
	private CacheDAO cacheDAO;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	{
		describe("Init", () -> {
			beforeEach(() -> {
				when(redisSettings.getCacheTtl()).thenReturn(100);
				when(redisTemplate.hasKey("test"))
						.thenThrow(new RuntimeException("Test"))
						.thenReturn(null, false, true);
				when(redisTemplate.opsForHash()).thenReturn(hashOperations);
				when(hashOperations.hasKey("test", "test"))
						.thenThrow(new RuntimeException("Test"))
						.thenReturn(null, false, true);
			});
			describe("CacheDAO 'exist' method test", () -> {
				it(
						"Should return throw exception",
						() -> assertThat(catchThrowable(() -> cacheDAO.exist("test")))
								.as("Check a key have error")
								.isInstanceOf(RuntimeException.class)
								.hasMessageContaining("Test")
				);
				it(
						"Should return null value",
						() -> assertThat(cacheDAO.exist("test")).as("Exist key is null").isNull()
				);
				it(
						"Should return false if key not found",
						() -> assertThat(cacheDAO.exist("test")).as("Key is not found").isFalse()
				);
				it(
						"Should return true if key found",
						() -> assertThat(cacheDAO.exist("test")).as("Key is found").isTrue()
				);
			});
			describe("CacheDAO 'hexist' method test", () -> {
				it(
						"Should return throw runtime exception",
						() -> assertThat(catchThrowable(() -> cacheDAO.hexist("test", "test")))
								.as("Check a key have error")
								.isInstanceOf(RuntimeException.class)
								.hasMessageContaining("Test")
				);
				it(
						"Should return false if hash is null",
						() -> assertThat(cacheDAO.hexist("test", null)).as("Check hash of null").isFalse()
				);
				it(
						"Should return null value",
						() -> assertThat(cacheDAO.hexist("test", "test")).as("Exist key is null").isNull()
				);
				it(
						"Should return false if key not found",
						() -> assertThat(cacheDAO.hexist("test", "test")).as("Key is not found").isFalse()
				);
				it(
						"Should return true if key found",
						() -> assertThat(cacheDAO.hexist("test", "test")).as("Key is found").isTrue()
				);
			});
		});
	}
}
