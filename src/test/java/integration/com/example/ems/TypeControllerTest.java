/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-04T12:07
 */
package integration.com.example.ems;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ems.database.dao.pg.TypesDAO;
import com.example.ems.dto.database.pg.Types;
import com.example.ems.dto.network.controller.Res;
import com.example.ems.dto.network.controller.ResError;
import com.example.ems.dto.network.controller.type.AllIn;
import com.example.ems.dto.network.controller.type.AllOut;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class TypeControllerTest extends RootControllerTest {
  @Autowired private RedisTemplate<Object, Object> redisTemplate;
  @Autowired private TypesDAO typesDAO;

  private Integer typeId;

  @BeforeEach
  void setUp() {
    Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushAll();
    typesDAO.deleteAll();
    Types type = new Types();
    type.setName("testName");
    type = typesDAO.save(type);
    typeId = type.getId();
  }

  @Test
  void all() {
    String rootPath = "/v1/type";
    String endpointExpected = String.format("%s%s", rootPath, "?name={name}");
    String endpointNameAndIDExpected = String.format("%s%s", rootPath, "?name={name}&id={id}");
    String notFound = "testName2";
    String ifNoneMatch;
    String ifNoneMatchWithoutQuotes;
    AllIn successReq = new AllIn();
    successReq.setName("testName");
    successReq.setId(typeId);
    successReq.setPath(String.format("/v1/type?name=%s&id=%s", "testName", typeId));
    String ifNoneMatchCacheKey =
        String.format("typeCache::all::ifNoneMatch::%s", successReq.toHashKey());
    Map<Integer, Map<String, Object>> errorsMap =
        new HashMap<>() {
          {
            put(
                422,
                new HashMap<>() {
                  {
                    put("message", "Request body or query or path params data is incorrect");
                    put("query", "");
                  }
                });
          }
        };

    // Test errors
    errorsMap.forEach(
        (k, v) -> {
          ResponseEntity<Res> responseEntity =
              this.restTemplate.getForEntity(
                  createURLWithPort(endpointExpected), Res.class, v.get("query"));
          assertThat(responseEntity.getStatusCodeValue())
              .as("Status code is incorrect")
              .isEqualTo(k);
          assertThat(responseEntity.getHeaders().getETag()).as("Etag is null").isNull();
          assertThat(responseEntity.getHeaders().getContentType())
              .as("Content type is JSON")
              .isEqualTo(MediaType.APPLICATION_JSON);
          assertThat(responseEntity.getBody())
              .as("Body is not null and is Res class")
              .isNotNull()
              .isInstanceOf(Res.class);
          Res<?> resBody = (Res<?>) responseEntity.getBody();
          assertThat(resBody.getTimestamp())
              .as("Timestamp is not null and is Long class")
              .isNotNull()
              .isInstanceOf(Long.class);
          assertThat(resBody.getResId())
              .as("Res ID is not null and is String class")
              .isNotNull()
              .isInstanceOf(String.class);
          assertThat(resBody.getData()).as("Data is null").isNull();
          assertThat(resBody.getError())
              .as("Error data is not null and ResError class")
              .isNotNull()
              .isInstanceOf(ResError.class);
          ResError resError = resBody.getError();
          assertThat(resError.getCode()).as("Error data code is incorrect").isEqualTo(k);
          assertThat(resError.getMethod()).as("Error data method is incorrect").isEqualTo("GET");
          assertThat(resError.getEndpoint())
              .as("Error data endpoint is incorrect")
              .isEqualTo(String.format("%s?name=%s", rootPath, v.get("query")));
          assertThat(resError.getMessage())
              .as("Error data message is incorrect")
              .isEqualTo(v.get("message"));
        });
    // Test if type not found by name
    ResponseEntity<Res> responseEntity =
        this.restTemplate.getForEntity(createURLWithPort(endpointExpected), Res.class, notFound);
    assertThat(responseEntity.getStatusCodeValue()).as("Status code is incorrect").isEqualTo(204);
    assertThat(responseEntity.getHeaders().getETag()).as("Etag is null").isNull();
    assertThat(responseEntity.getHeaders().getContentType())
        .as("Content type is JSON")
        .isEqualTo(MediaType.APPLICATION_JSON);
    assertThat(responseEntity.getBody()).as("Body is null").isNull();
    assertThat(redisTemplate.hasKey(ifNoneMatchCacheKey)).as("Data in cache not found").isFalse();

    // Test if type found by name and ID
    responseEntity =
        this.restTemplate.getForEntity(
            createURLWithPort(endpointNameAndIDExpected),
            Res.class,
            successReq.getName(),
            successReq.getId());
    assertThat(responseEntity.getStatusCodeValue()).as("Status code is incorrect").isEqualTo(200);
    assertThat(responseEntity.getHeaders().getETag())
        .as("Etag is not null")
        .isNotNull()
        .isInstanceOf(String.class);
    ifNoneMatch = responseEntity.getHeaders().getETag();
    ifNoneMatchWithoutQuotes = ifNoneMatch.substring(1, ifNoneMatch.length() - 1);
    assertThat(responseEntity.getHeaders().getContentType())
        .as("Content type is JSON")
        .isEqualTo(MediaType.APPLICATION_JSON);
    assertThat(responseEntity.getBody())
        .as("Body is not null and is Res class")
        .isNotNull()
        .isInstanceOf(Res.class);
    Res<?> resBody = (Res<?>) responseEntity.getBody();
    assertThat(resBody.getTimestamp())
        .as("Timestamp is not null and is Long class")
        .isNotNull()
        .isInstanceOf(Long.class);
    assertThat(resBody.getResId())
        .as("Res ID is not null and is String class")
        .isNotNull()
        .isInstanceOf(String.class);
    assertThat(resBody.getError()).as("Error data is null").isNull();
    assertThat(resBody.getData())
        .as("Data is not null and Object class")
        .isNotNull()
        .isInstanceOf(Object.class);
    List<?> resData = mapper.convertValue(resBody.getData(), List.class);
    assertThat(resData).as("Check list data is empty").isNotEmpty();
    Types type = mapper.convertValue(resData.get(0), Types.class);
    assertThat(type).as("Type is not null").isNotNull();
    assertThat(type.getId()).as("Type ID").isEqualTo(typeId);
    assertThat(type.getName()).as("Type name").isEqualTo("testName");
    assertThat(redisTemplate.hasKey(ifNoneMatchCacheKey)).as("Check data in cache").isTrue();
    AllOut<?> cache = (AllOut<?>) redisTemplate.boundValueOps(ifNoneMatchCacheKey).get();
    assertThat(cache).as("Cache is not null").isNotNull();
    assertThat(cache.getEtag()).as("Cache etag").isEqualTo(ifNoneMatchWithoutQuotes);
    List<?> cacheData = cache.getData();
    assertThat(cacheData).as("Cache data is not null and not is empty").isNotNull().isNotEmpty();
    type = mapper.convertValue(resData.get(0), Types.class);
    assertThat(type).as("Type is not null cache").isNotNull();
    assertThat(type.getId()).as("Type ID cache").isEqualTo(typeId);
    assertThat(type.getName()).as("Type name cache").isEqualTo("testName");

    // If data is not change
    headers.setIfNoneMatch(ifNoneMatch);
    HttpEntity<Res> entity = new HttpEntity<>(null, headers);
    responseEntity =
        this.restTemplate.exchange(
            createURLWithPort(endpointNameAndIDExpected),
            HttpMethod.GET,
            entity,
            Res.class,
            successReq.getName(),
            successReq.getId());
    assertThat(responseEntity.getStatusCodeValue()).as("Status code is incorrect").isEqualTo(304);
    assertThat(responseEntity.getHeaders().getETag()).as("Etag is null").isNull();
    assertThat(responseEntity.getHeaders().getContentType()).as("Content type is null").isNull();
    assertThat(responseEntity.getBody()).as("Body is null").isNull();

    String ifMatchCacheKey =
        String.format("typeCache::all::forMatch::%s", ifNoneMatchWithoutQuotes);
    assertThat(redisTemplate.hasKey(ifMatchCacheKey)).as("Check match etag").isTrue();
  }
}
