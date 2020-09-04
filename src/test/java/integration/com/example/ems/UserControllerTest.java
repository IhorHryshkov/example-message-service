/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-04T12:19
 */
package integration.com.example.ems;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ems.database.dao.pg.StatusDAO;
import com.example.ems.database.dao.pg.TypesDAO;
import com.example.ems.database.dao.pg.UsersDAO;
import com.example.ems.database.dao.redis.StateDAO;
import com.example.ems.dto.database.pg.Status;
import com.example.ems.dto.database.pg.Types;
import com.example.ems.dto.database.pg.Users;
import com.example.ems.dto.network.controller.Res;
import com.example.ems.dto.network.controller.ResError;
import com.example.ems.dto.network.controller.user.AllIn;
import com.example.ems.dto.network.controller.user.AllOut;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class UserControllerTest extends RootControllerTest {
  @Autowired private RedisTemplate<Object, Object> redisTemplate;
  @Autowired private AmqpAdmin amqpAdmin;
  @Autowired private UsersDAO usersDAO;
  @Autowired private TypesDAO typeDAO;
  @Autowired private StatusDAO statusDAO;
  @Autowired private StateDAO stateDAO;
  private String userId;
  private Integer typeId;
  private Integer statusId;

  @BeforeEach
  void setUp() {
    Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushAll();
    usersDAO.deleteAll();
    typeDAO.deleteAll();
    statusDAO.deleteAll();
    amqpAdmin.deleteQueue("user.add.testUser");
    amqpAdmin.deleteQueue("user.update.testUser");
    amqpAdmin.deleteQueue("websocket.testUser");
    Status status = new Status();
    status.setName("testName");
    status = statusDAO.save(status);
    Types type = new Types();
    type.setName("testName");
    type = typeDAO.save(type);
    Users user = new Users("testUser", status);
    user = usersDAO.save(user);
    typeId = type.getId();
    userId = user.getId().toString();
    statusId = status.getId();
  }

  @Test
  void all() {
    String rootPath = "/v1/user";
    String endpointExpected = String.format("%s%s", rootPath, "?username={name}");
    String endpointNameAndIDExpected =
        String.format("%s%s", rootPath, "?username={name}&userId={id}");
    String notFound = "testUser2";
    String ifNoneMatch;
    String ifNoneMatchWithoutQuotes;
    AllIn successReq = new AllIn();
    successReq.setUsername("testUser");
    successReq.setUserId(userId);
    successReq.setPath(String.format("%s?username=%s&userId=%s", rootPath, "testUser", userId));
    String ifNoneMatchCacheKey =
        String.format("userCache::all::ifNoneMatch::%s", successReq.toHashKey());
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
              .isEqualTo(String.format("%s?username=%s", rootPath, v.get("query")));
          assertThat(resError.getMessage())
              .as("Error data message is incorrect")
              .isEqualTo(v.get("message"));
        });
    // Test if user not found by username
    ResponseEntity<Res> responseEntity =
        this.restTemplate.getForEntity(createURLWithPort(endpointExpected), Res.class, notFound);
    assertThat(responseEntity.getStatusCodeValue()).as("Status code is incorrect").isEqualTo(204);
    assertThat(responseEntity.getHeaders().getETag()).as("Etag is null").isNull();
    assertThat(responseEntity.getHeaders().getContentType())
        .as("Content type is JSON")
        .isEqualTo(MediaType.APPLICATION_JSON);
    assertThat(responseEntity.getBody()).as("Body is null").isNull();
    assertThat(redisTemplate.hasKey(ifNoneMatchCacheKey)).as("Data in cache not found").isFalse();

    // Test if user found by username and ID
    responseEntity =
        this.restTemplate.getForEntity(
            createURLWithPort(endpointNameAndIDExpected),
            Res.class,
            successReq.getUsername(),
            successReq.getUserId().toString());
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
    Users user = mapper.convertValue(resData.get(0), Users.class);
    assertThat(user).as("User is not null").isNotNull();
    assertThat(user.getId().toString()).as("User ID").isEqualTo(userId);
    assertThat(user.getUsername()).as("User username").isEqualTo("testUser");
    assertThat(user.getStatus()).as("User status is not null").isNotNull();
    assertThat(user.getStatus().getId()).as("User status ID").isEqualTo(statusId);
    assertThat(user.getStatus().getName()).as("User status name").isEqualTo("testName");

    assertThat(redisTemplate.hasKey(ifNoneMatchCacheKey)).as("Check data in cache").isTrue();
    AllOut<?> cache = (AllOut<?>) redisTemplate.boundValueOps(ifNoneMatchCacheKey).get();
    assertThat(cache).as("Cache is not null").isNotNull();
    assertThat(cache.getEtag()).as("Cache etag").isEqualTo(ifNoneMatchWithoutQuotes);
    List<?> cacheData = cache.getData();
    assertThat(cacheData).as("Cache data is not null and not is empty").isNotNull().isNotEmpty();
    user = mapper.convertValue(resData.get(0), Users.class);
    assertThat(user).as("Users is not null cache").isNotNull();
    assertThat(user.getId().toString()).as("User ID cache").isEqualTo(userId);
    assertThat(user.getUsername()).as("User username cache").isEqualTo("testUser");
    assertThat(user.getStatus()).as("User status is not null cache").isNotNull();
    assertThat(user.getStatus().getId()).as("User status ID cache").isEqualTo(statusId);
    assertThat(user.getStatus().getName()).as("User status name cache").isEqualTo("testName");

    // If data is not change
    headers.setIfNoneMatch(ifNoneMatch);
    HttpEntity<Res> entity = new HttpEntity<>(null, headers);
    responseEntity =
        this.restTemplate.exchange(
            createURLWithPort(endpointNameAndIDExpected),
            HttpMethod.GET,
            entity,
            Res.class,
            successReq.getUsername(),
            successReq.getUserId().toString());
    assertThat(responseEntity.getStatusCodeValue()).as("Status code is incorrect").isEqualTo(304);
    assertThat(responseEntity.getHeaders().getETag()).as("Etag is null").isNull();
    assertThat(responseEntity.getHeaders().getContentType()).as("Content type is null").isNull();
    assertThat(responseEntity.getBody()).as("Body is null").isNull();

    String ifMatchCacheKey =
        String.format("userCache::all::forMatch::%s", ifNoneMatchWithoutQuotes);
    assertThat(redisTemplate.hasKey(ifMatchCacheKey)).as("Check match etag").isTrue();
  }
}
