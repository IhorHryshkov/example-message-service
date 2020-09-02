/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-01T10:34
 */
package integration.com.example.ems;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ems.database.dao.pg.CountersDAO;
import com.example.ems.database.dao.pg.StatusDAO;
import com.example.ems.database.dao.pg.TypesDAO;
import com.example.ems.database.dao.pg.UsersDAO;
import com.example.ems.dto.database.pg.Counters;
import com.example.ems.dto.database.pg.Status;
import com.example.ems.dto.database.pg.Types;
import com.example.ems.dto.database.pg.Users;
import com.example.ems.dto.database.pg.ids.CountersIds;
import com.example.ems.dto.network.controller.Res;
import com.example.ems.dto.network.controller.ResError;
import com.example.ems.dto.network.controller.counter.GetByIdIn;
import com.example.ems.dto.network.controller.counter.GetByIdOut;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;

public class CounterControllerTest extends RootControllerTest {
  @Autowired private RedisTemplate<Object, Object> redisTemplate;
  @Autowired private CountersDAO countersDAO;
  @Autowired private UsersDAO usersDAO;
  @Autowired private TypesDAO typeDAO;
  @Autowired private StatusDAO statusDAO;
  private String userId;
  private Integer typeId;
  private Integer statusId;
  private CountersIds countersIds;

  @BeforeEach
  void setUp() {
    Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushAll();
    countersDAO.deleteAll();
    usersDAO.deleteAll();
    typeDAO.deleteAll();
    statusDAO.deleteAll();
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
    countersIds = new CountersIds(user.getId(), type.getId());
    Counters counters = new Counters();
    counters.setKeys(countersIds);
    counters.setCounts(BigInteger.valueOf(10L));
    counters.setUser(user);
    counters.setType(type);
    countersDAO.save(counters);
  }

  @Test
  void getById() {
    String endpointExpected = "/v1/counter";
    String notFound = "319d5c19-1187-457e-8080-b36f28fdccb0";
    String ifNoneMatch;
    String ifNoneMatchWithoutQuotes;
    GetByIdIn successReq = new GetByIdIn();
    successReq.setUserId(userId);
    String ifNoneMatchCacheKey =
        String.format("counterCache::getByUserId::ifNoneMatch::%s", successReq.toHashKey());
    String ifMatchCacheKey =
        String.format("counterCache::getById::forMatch::%s", successReq.toHashKey());
    Map<Integer, Map<String, Object>> errorsMap =
        new HashMap<>() {
          {
            put(
                404,
                new HashMap<>() {
                  {
                    put("message", "Request endpoint not supported");
                    put("pathParam", "");
                  }
                });
            put(
                422,
                new HashMap<>() {
                  {
                    put("message", "Request body or query or path params data is incorrect");
                    put("pathParam", "testUser");
                  }
                });
          }
        };

    // Test errors
    errorsMap.forEach(
        (k, v) -> {
          ResponseEntity<Res> responseEntity =
              this.restTemplate.getForEntity(
                  createURLWithPort(endpointExpected, v.get("pathParam")), Res.class);
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
              .isEqualTo(String.format("%s/%s", endpointExpected, v.get("pathParam")));
          assertThat(resError.getMessage())
              .as("Error data message is incorrect")
              .isEqualTo(v.get("message"));
        });
    // Test if counter not found by user ID
    ResponseEntity<Res> responseEntity =
        this.restTemplate.getForEntity(createURLWithPort(endpointExpected, notFound), Res.class);
    assertThat(responseEntity.getStatusCodeValue()).as("Status code is incorrect").isEqualTo(204);
    assertThat(responseEntity.getHeaders().getETag()).as("Etag is null").isNull();
    assertThat(responseEntity.getHeaders().getContentType())
        .as("Content type is JSON")
        .isEqualTo(MediaType.APPLICATION_JSON);
    assertThat(responseEntity.getBody()).as("Body is null").isNull();
    assertThat(redisTemplate.hasKey(ifNoneMatchCacheKey)).as("Data in cache not found").isFalse();

    // Test if counter found by user ID
    responseEntity =
        this.restTemplate.getForEntity(createURLWithPort(endpointExpected, userId), Res.class);
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
    Counters counter = mapper.convertValue(resData.get(0), Counters.class);
    assertThat(counter.getCounts()).as("").isEqualTo(10);
    assertThat(counter.getKeys()).as("Keys is null").isNull();
    assertThat(counter.getType())
        .as("Type is not null and Types class")
        .isNotNull()
        .isInstanceOf(Types.class);
    assertThat(counter.getUser())
        .as("User is not null and Users class")
        .isNotNull()
        .isInstanceOf(Users.class);
    assertThat(counter.getType().getName()).as("Type name").isEqualTo("testName");
    assertThat(counter.getType().getId()).as("Type ID").isEqualTo(typeId);
    assertThat(counter.getUser().getUsername()).as("Username").isEqualTo("testUser");
    assertThat(counter.getUser().getId()).as("User ID").isEqualTo(UUID.fromString(userId));
    assertThat(counter.getUser().getStatus())
        .as("Status is not null and Status class")
        .isNotNull()
        .isInstanceOf(Status.class);
    assertThat(counter.getUser().getStatus().getName()).as("Status name").isEqualTo("testName");
    assertThat(counter.getUser().getStatus().getId()).as("Status ID").isEqualTo(statusId);
    assertThat(redisTemplate.hasKey(ifNoneMatchCacheKey)).as("Check data in cache").isTrue();
    GetByIdOut<?> cache = (GetByIdOut<?>) redisTemplate.boundValueOps(ifNoneMatchCacheKey).get();
    assertThat(cache).as("Cache is not null").isNotNull();
    assertThat(cache.getEtag()).as("Cache etag").isEqualTo(ifNoneMatchWithoutQuotes);
    List<?> cacheData = (List<?>) cache.getData();
    assertThat(cacheData).as("Cache data is not null and not is empty").isNotNull().isNotEmpty();
    counter = (Counters) cacheData.get(0);
    assertThat(counter.getCounts()).as("").isEqualTo(10);
    assertThat(counter.getKeys()).as("Keys is not null cache").isNotNull();
    assertThat(counter.getKeys().getTypeId()).as("Keys type ID cache").isEqualTo(typeId);
    assertThat(counter.getKeys().getUserId())
        .as("Keys user ID cache")
        .isEqualTo(UUID.fromString(userId));
    assertThat(counter.getType())
        .as("Type is not null and Types class cache")
        .isNotNull()
        .isInstanceOf(Types.class);
    assertThat(counter.getUser())
        .as("User is not null and Users class cache")
        .isNotNull()
        .isInstanceOf(Users.class);
    assertThat(counter.getType().getName()).as("Type name cache").isEqualTo("testName");
    assertThat(counter.getType().getId()).as("Type ID cache").isEqualTo(typeId);
    assertThat(counter.getUser().getUsername()).as("Username cache").isEqualTo("testUser");
    assertThat(counter.getUser().getId()).as("User ID cache").isEqualTo(UUID.fromString(userId));
    assertThat(counter.getUser().getStatus())
        .as("Status is not null and Status class cache")
        .isNotNull()
        .isInstanceOf(Status.class);
    assertThat(counter.getUser().getStatus().getName())
        .as("Status name cache")
        .isEqualTo("testName");
    assertThat(counter.getUser().getStatus().getId()).as("Status ID cache").isEqualTo(statusId);

    headers.setIfNoneMatch(ifNoneMatch);
    HttpEntity<Res> entity = new HttpEntity<>(null, headers);
    responseEntity =
        this.restTemplate.exchange(
            createURLWithPort(endpointExpected, userId), HttpMethod.GET, entity, Res.class);
    assertThat(responseEntity.getStatusCodeValue()).as("Status code is incorrect").isEqualTo(304);
    assertThat(responseEntity.getHeaders().getETag()).as("Etag is null").isNull();
    assertThat(responseEntity.getHeaders().getContentType()).as("Content type is null").isNull();
    assertThat(responseEntity.getBody()).as("Body is null").isNull();

    assertThat(redisTemplate.opsForHash().hasKey(ifMatchCacheKey, ifNoneMatchWithoutQuotes))
        .as("Check match etag")
        .isTrue();
    counter = countersDAO.findById(countersIds).orElse(null);
    assertThat(counter).as("DB counter is not null by user and type ID").isNotNull();
    assertThat(counter.getCounts()).as("").isEqualTo(10);
    assertThat(counter.getKeys()).as("Keys is not null DB").isNotNull();
    assertThat(counter.getKeys().getTypeId()).as("Keys type ID DB").isEqualTo(typeId);
    assertThat(counter.getKeys().getUserId())
        .as("Keys user ID DB")
        .isEqualTo(UUID.fromString(userId));
    assertThat(counter.getType())
        .as("Type is not null and Types class DB")
        .isNotNull()
        .isInstanceOf(Types.class);
    assertThat(counter.getUser())
        .as("User is not null and Users class DB")
        .isNotNull()
        .isInstanceOf(Users.class);
    assertThat(counter.getType().getName()).as("Type name DB").isEqualTo("testName");
    assertThat(counter.getType().getId()).as("Type ID DB").isEqualTo(typeId);
    assertThat(counter.getUser().getUsername()).as("Username DB").isEqualTo("testUser");
    assertThat(counter.getUser().getId()).as("User ID DB").isEqualTo(UUID.fromString(userId));
    assertThat(counter.getUser().getStatus())
        .as("Status is not null and Status class DB")
        .isNotNull()
        .isInstanceOf(Status.class);
    assertThat(counter.getUser().getStatus().getName()).as("Status name DB").isEqualTo("testName");
    assertThat(counter.getUser().getStatus().getId()).as("Status ID DB").isEqualTo(statusId);
  }

  @Test
  void add() throws InterruptedException, ExecutionException, TimeoutException {
    WebSocketStompClient stompClient =
        new WebSocketStompClient(new SockJsClient(createTransportClient()));
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    StompSession stompSession =
        stompClient
            .connect("http://localhost:31111/v1/callback", new CustomStompSessionHandler())
            .get();
  }
}
