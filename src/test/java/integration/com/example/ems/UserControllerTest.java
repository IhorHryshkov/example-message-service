/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-04T12:19
 */
package integration.com.example.ems;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ems.database.dao.pg.StatusDAO;
import com.example.ems.database.dao.pg.UsersDAO;
import com.example.ems.database.dao.redis.StateDAO;
import com.example.ems.dto.database.pg.Status;
import com.example.ems.dto.database.pg.Users;
import com.example.ems.dto.network.controller.Callback;
import com.example.ems.dto.network.controller.Res;
import com.example.ems.dto.network.controller.ResError;
import com.example.ems.dto.network.controller.State;
import com.example.ems.dto.network.controller.user.AddIn;
import com.example.ems.dto.network.controller.user.AddOut;
import com.example.ems.dto.network.controller.user.AllIn;
import com.example.ems.dto.network.controller.user.AllOut;
import com.example.ems.dto.network.controller.user.UpdateIn;
import com.example.ems.dto.network.controller.user.UpdateOut;
import com.example.ems.utils.enums.States;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;

public class UserControllerTest extends RootControllerTest {
  @Autowired private RedisTemplate<Object, Object> redisTemplate;
  @Autowired private AmqpAdmin amqpAdmin;
  @Autowired private UsersDAO usersDAO;
  @Autowired private StatusDAO statusDAO;
  @Autowired private StateDAO stateDAO;
  private String userId;
  private Integer statusId;
  private Integer statusIdOnline;
  private String userIdOnline;

  @BeforeEach
  void setUp() {
    String[] queues = {
      "user.add.testUser2",
      "user.update.testUserOnline",
      "websocket.testUser",
      "websocket.testUser2",
      "websocket.testUserOnline"
    };
    for (String queueName : queues) {
      if (amqpAdmin.getQueueInfo(queueName) != null) {
        amqpAdmin.purgeQueue(queueName);
      }
    }
    Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushAll();
    usersDAO.deleteAll();
    statusDAO.deleteAll();
    Status status = new Status();
    status.setName("testName");
    status = statusDAO.save(status);
    statusId = status.getId();
    Status statusOnline = new Status();
    statusOnline.setName("online");
    statusOnline = statusDAO.save(statusOnline);
    statusIdOnline = statusOnline.getId();
    Users user = new Users("testUser", status);
    user = usersDAO.save(user);
    userId = user.getId().toString();
    Users userOnline = new Users("testUserOnline", statusOnline);
    userOnline = usersDAO.save(userOnline);
    userIdOnline = userOnline.getId().toString();
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
          Res resBody = responseEntity.getBody();
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
    Res resBody = responseEntity.getBody();
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

  @Test
  void add() throws InterruptedException, ExecutionException, TimeoutException {
    String endpointExpected = "/v1/user";
    String callbackExpected = "/v1/callback";
    String endpointCallbackApprove = String.format("%s%s", callbackExpected, "/approve");
    AddIn params201 = new AddIn();
    params201.setUsername("testUser2");
    AddIn params202 = new AddIn();
    params202.setUsername("testUserInProgress");
    AddIn params202InDb = new AddIn();
    params202InDb.setUsername("testUserOnline");
    Callback callbackSuccess = new Callback();

    Map<Integer, Map<String, Object>> errorsMap =
        new HashMap<>() {
          {
            put(
                500,
                new HashMap<>() {
                  {
                    put("message", "Internal server error, please try again later.");
                    put("body", null);
                  }
                });
            put(
                422,
                new HashMap<>() {
                  {
                    put("message", "Request body or query or path params data is incorrect");
                    put("body", new AddIn());
                  }
                });
          }
        };
    errorsMap.forEach(
        (k, v) -> {
          ResponseEntity<Res> responseEntity =
              this.restTemplate.postForEntity(
                  createURLWithPort(endpointExpected), v.get("body"), Res.class);
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
          Res resBody = responseEntity.getBody();
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
          assertThat(resError.getMethod()).as("Error data method is incorrect").isEqualTo("POST");
          assertThat(resError.getEndpoint())
              .as("Error data endpoint is incorrect")
              .isEqualTo(endpointExpected);
          assertThat(resError.getMessage())
              .as("Error data message is incorrect")
              .isEqualTo(v.get("message"));
        });
    // Test if user have in DB
    ResponseEntity<Res> responseEntity =
        this.restTemplate.postForEntity(
            createURLWithPort(endpointExpected), params202InDb, Res.class);
    assertThat(responseEntity.getStatusCodeValue()).as("Status code is incorrect").isEqualTo(202);
    assertThat(responseEntity.getHeaders().getETag())
        .as("Etag is not null")
        .isNotNull()
        .isInstanceOf(String.class);
    assertThat(responseEntity.getHeaders().getContentType())
        .as("Content type is JSON")
        .isEqualTo(MediaType.APPLICATION_JSON);
    assertThat(responseEntity.getBody())
        .as("Body is not null and is Res class")
        .isNotNull()
        .isInstanceOf(Res.class);
    Res resBody = responseEntity.getBody();
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
    State resData = mapper.convertValue(resBody.getData(), State.class);
    assertThat(resData.getState())
        .as("Data state is in progress")
        .isEqualTo(States.IN_PROGRESS.toString());

    // Test if add user state in progress
    stateDAO.add(
        "userState::add::IN_PROGRESS",
        "7d62e84109b0ca8224325127b1d4786a05f284bc6565c0e0820017678f2ef569",
        "testData");
    responseEntity =
        this.restTemplate.postForEntity(createURLWithPort(endpointExpected), params202, Res.class);
    assertThat(responseEntity.getStatusCodeValue()).as("Status code is incorrect").isEqualTo(202);
    assertThat(responseEntity.getHeaders().getETag())
        .as("Etag is not null")
        .isNotNull()
        .isInstanceOf(String.class);
    assertThat(responseEntity.getHeaders().getContentType())
        .as("Content type is JSON")
        .isEqualTo(MediaType.APPLICATION_JSON);
    assertThat(responseEntity.getBody())
        .as("Body is not null and is Res class")
        .isNotNull()
        .isInstanceOf(Res.class);
    resBody = responseEntity.getBody();
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
    resData = mapper.convertValue(resBody.getData(), State.class);
    assertThat(resData.getState())
        .as("Data state is in progress")
        .isEqualTo(States.IN_PROGRESS.toString());
    Users user = usersDAO.findByUsername(params202.getUsername()).stream().findFirst().orElse(null);
    assertThat(user).as("DB user do not add by username").isNull();

    // Test if add user resolve
    WebSocketStompClient stompClient =
        new WebSocketStompClient(new SockJsClient(createTransportClient()));
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    StompSession stompSession =
        stompClient
            .connect(createURLWithPort(callbackExpected), new StompSessionHandlerAdapter() {})
            .get();
    stompSession.subscribe(
        String.format("/queue/%s", params201.getUsername()), new CustomStompFrameHandler());
    responseEntity =
        this.restTemplate.postForEntity(createURLWithPort(endpointExpected), params201, Res.class);
    assertThat(responseEntity.getStatusCodeValue()).as("Status code is incorrect").isEqualTo(201);
    assertThat(responseEntity.getHeaders().getETag())
        .as("Etag is not null")
        .isNotNull()
        .isInstanceOf(String.class);
    assertThat(responseEntity.getHeaders().getContentType())
        .as("Content type is JSON")
        .isEqualTo(MediaType.APPLICATION_JSON);
    assertThat(responseEntity.getBody())
        .as("Body is not null and is Res class")
        .isNotNull()
        .isInstanceOf(Res.class);
    resBody = responseEntity.getBody();
    assertThat(resBody.getTimestamp())
        .as("Timestamp is not null and is Long class")
        .isNotNull()
        .isInstanceOf(Long.class);
    assertThat(resBody.getResId())
        .as("Res ID is not null and is String class")
        .isNotNull()
        .isInstanceOf(String.class);
    String resIdSuccess = resBody.getResId();
    callbackSuccess.setResId(resIdSuccess);
    assertThat(resBody.getError()).as("Error data is null").isNull();
    assertThat(resBody.getData())
        .as("Data is not null and Object class")
        .isNotNull()
        .isInstanceOf(Object.class);
    AddIn resDataSuccess = mapper.convertValue(resBody.getData(), AddIn.class);
    assertThat(resDataSuccess.getUsername()).as("Username").isEqualTo("testUser2");
    assertThat(resDataSuccess.getResId()).as("Res ID is null").isNull();

    Object callback = completableFuture.get(10, TimeUnit.SECONDS);
    assertThat(callback).as("Callback is not null").isNotNull();
    AddOut addOut = mapper.convertValue(callback, AddOut.class);
    assertThat(addOut.getResId()).as("Out Res ID").isEqualTo(resIdSuccess);
    assertThat(addOut.getUserId()).as("Out User ID is not null").isNotNull();

    String newUserId = addOut.getUserId();

    responseEntity =
        this.restTemplate.postForEntity(
            createURLWithPort(endpointCallbackApprove), callbackSuccess, Res.class);
    assertThat(responseEntity.getStatusCodeValue()).as("Status code is incorrect").isEqualTo(200);
    assertThat(
            redisTemplate.opsForHash().hasKey("userState::add::IN_PROGRESS", params201.toHashKey()))
        .as("Counter add in progress is not found")
        .isFalse();
    assertThat(redisTemplate.opsForHash().hasKey("state::callback::RESOLVE", resIdSuccess))
        .as("State RESOLVE is add")
        .isTrue();
    assertThat(redisTemplate.opsForHash().hasKey("state::callback::RESOLVE::expire", resIdSuccess))
        .as("State RESOLVE expire is add")
        .isTrue();
    assertThat(redisTemplate.opsForHash().hasKey("state::callback::IN_PROGRESS", resIdSuccess))
        .as("State IN_PROGRESS is not del")
        .isFalse();
    user = usersDAO.findByUsername(params201.getUsername()).stream().findFirst().orElse(null);
    assertThat(user).as("Users is not null DB").isNotNull();
    assertThat(user.getId().toString()).as("User ID DB").isEqualTo(newUserId);
    assertThat(user.getUsername()).as("User username DB").isEqualTo("testUser2");
    assertThat(user.getStatus()).as("User status is not null DB").isNotNull();
    assertThat(user.getStatus().getId()).as("User status ID DB").isEqualTo(statusIdOnline);
    assertThat(user.getStatus().getName()).as("User status name DB").isEqualTo("online");
  }

  @Test
  void update() throws InterruptedException, ExecutionException, TimeoutException {
    String endpointExpected = "/v1/user";
    String callbackExpected = "/v1/callback";
    String endpointCallbackApprove = String.format("%s%s", callbackExpected, "/approve");
    UpdateIn params200 = new UpdateIn();
    params200.setStatusId(statusId);
    UpdateIn params202 = new UpdateIn();
    params202.setStatusId(statusIdOnline);
    UpdateIn params202Hash = new UpdateIn();
    params202Hash.setStatusId(statusIdOnline);
    params202Hash.setUserId(userId);
    UpdateIn params200Hash = new UpdateIn();
    params200Hash.setStatusId(statusId);
    params200Hash.setUserId(userIdOnline);
    Callback callbackSuccess = new Callback();

    Map<Integer, Map<String, Object>> errorsMap =
        new HashMap<>() {
          {
            put(
                500,
                new HashMap<>() {
                  {
                    put("message", "Internal server error, please try again later.");
                    put("body", null);
                    put("pathParam", null);
                  }
                });
            put(
                422,
                new HashMap<>() {
                  {
                    put("message", "Request body or query or path params data is incorrect");
                    put("body", new UpdateIn());
                    put("pathParam", "testTest");
                  }
                });
            put(
                400,
                new HashMap<>() {
                  {
                    put("message", "User ID not found");
                    put("body", params202);
                    put("pathParam", "bbbbbbbb-1187-457e-8080-b36f28fdccb0");
                  }
                });
          }
        };
    errorsMap.forEach(
        (k, v) -> {
          HttpEntity<Object> entity = new HttpEntity<>(v.get("body"), headers);
          ResponseEntity<Res> responseEntity =
              this.restTemplate.exchange(
                  createURLWithPort(endpointExpected, v.get("pathParam")),
                  HttpMethod.PUT,
                  entity,
                  Res.class);
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
          Res resBody = responseEntity.getBody();
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
          assertThat(resError.getMethod()).as("Error data method is incorrect").isEqualTo("PUT");
          assertThat(resError.getEndpoint())
              .as("Error data endpoint is incorrect")
              .isEqualTo(String.format("%s/%s", endpointExpected, v.get("pathParam")));
          assertThat(resError.getMessage())
              .as("Error data message is incorrect")
              .isEqualTo(v.get("message"));
        });

    // Test if update user status in progress
    stateDAO.add(
        "userState::updateCounterAndStatus::IN_PROGRESS", params202Hash.toHashKey(), "testData");
    HttpEntity<UpdateIn> entity = new HttpEntity<>(params202, headers);
    ResponseEntity<Res> responseEntity =
        this.restTemplate.exchange(
            createURLWithPort(endpointExpected, userId), HttpMethod.PUT, entity, Res.class);
    assertThat(responseEntity.getStatusCodeValue()).as("Status code is incorrect").isEqualTo(202);
    assertThat(responseEntity.getHeaders().getETag())
        .as("Etag is not null")
        .isNotNull()
        .isInstanceOf(String.class);
    assertThat(responseEntity.getHeaders().getContentType())
        .as("Content type is JSON")
        .isEqualTo(MediaType.APPLICATION_JSON);
    assertThat(responseEntity.getBody())
        .as("Body is not null and is Res class")
        .isNotNull()
        .isInstanceOf(Res.class);
    Res resBody = responseEntity.getBody();
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
    State resData = mapper.convertValue(resBody.getData(), State.class);
    assertThat(resData.getState())
        .as("Data state is in progress")
        .isEqualTo(States.IN_PROGRESS.toString());
    Users user = usersDAO.findById(UUID.fromString(userId)).orElse(null);
    assertThat(user).as("DB user is not null").isNotNull();
    assertThat(user.getStatus()).as("DB user status is not null").isNotNull();
    assertThat(user.getStatus().getId()).as("DB user status ID").isNotEqualTo(statusIdOnline);
    assertThat(user.getStatus().getName()).as("DB user status name").isNotEqualTo("online");

    // Test if update user resolve
    WebSocketStompClient stompClient =
        new WebSocketStompClient(new SockJsClient(createTransportClient()));
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    StompSession stompSession =
        stompClient
            .connect(createURLWithPort(callbackExpected), new StompSessionHandlerAdapter() {})
            .get();
    stompSession.subscribe(
        String.format("/queue/%s", "testUserOnline"), new CustomStompFrameHandler());
    entity = new HttpEntity<>(params200, headers);
    responseEntity =
        this.restTemplate.exchange(
            createURLWithPort(endpointExpected, userIdOnline), HttpMethod.PUT, entity, Res.class);
    assertThat(responseEntity.getStatusCodeValue()).as("Status code is incorrect").isEqualTo(200);
    assertThat(responseEntity.getHeaders().getETag())
        .as("Etag is not null")
        .isNotNull()
        .isInstanceOf(String.class);
    assertThat(responseEntity.getHeaders().getContentType())
        .as("Content type is JSON")
        .isEqualTo(MediaType.APPLICATION_JSON);
    assertThat(responseEntity.getBody())
        .as("Body is not null and is Res class")
        .isNotNull()
        .isInstanceOf(Res.class);
    resBody = responseEntity.getBody();
    assertThat(resBody.getTimestamp())
        .as("Timestamp is not null and is Long class")
        .isNotNull()
        .isInstanceOf(Long.class);
    assertThat(resBody.getResId())
        .as("Res ID is not null and is String class")
        .isNotNull()
        .isInstanceOf(String.class);
    String resIdSuccess = resBody.getResId();
    callbackSuccess.setResId(resIdSuccess);
    assertThat(resBody.getError()).as("Error data is null").isNull();
    assertThat(resBody.getData())
        .as("Data is not null and Object class")
        .isNotNull()
        .isInstanceOf(Object.class);
    UpdateIn resDataSuccess = mapper.convertValue(resBody.getData(), UpdateIn.class);
    assertThat(resDataSuccess.getUserId()).as("User ID").isEqualTo(UUID.fromString(userIdOnline));
    assertThat(resDataSuccess.getStatusId()).as("Status ID").isEqualTo(statusId);
    assertThat(resDataSuccess.getResId()).as("Res ID is null").isNull();

    Object callback = completableFuture.get(10, TimeUnit.SECONDS);
    assertThat(callback).as("Callback is not null").isNotNull();
    UpdateOut updateOut = mapper.convertValue(callback, UpdateOut.class);
    assertThat(updateOut.getResId()).as("Out Res ID").isEqualTo(resIdSuccess);
    assertThat(updateOut.getUserId()).as("Out User ID ").isEqualTo(userIdOnline);

    responseEntity =
        this.restTemplate.postForEntity(
            createURLWithPort(endpointCallbackApprove), callbackSuccess, Res.class);
    assertThat(responseEntity.getStatusCodeValue()).as("Status code is incorrect").isEqualTo(200);
    assertThat(
            redisTemplate
                .opsForHash()
                .hasKey("userState::updateCounterAndStatus::INIT", params200Hash.toHashKey()))
        .as("Counter add init is not found")
        .isFalse();
    assertThat(
            redisTemplate
                .opsForHash()
                .hasKey(
                    "userState::updateCounterAndStatus::IN_PROGRESS", params200Hash.toHashKey()))
        .as("Counter add in progress is not found")
        .isFalse();
    assertThat(
            redisTemplate.hasKey(
                String.format(
                    "userCache::getUserOrNotFound::ifNoneMatch::%s", params200Hash.toHashUserId())))
        .as("User get by ID found cache")
        .isTrue();
    assertThat(redisTemplate.opsForHash().hasKey("state::callback::RESOLVE", resIdSuccess))
        .as("State RESOLVE is add")
        .isTrue();
    assertThat(redisTemplate.opsForHash().hasKey("state::callback::RESOLVE::expire", resIdSuccess))
        .as("State RESOLVE expire is add")
        .isTrue();
    assertThat(redisTemplate.opsForHash().hasKey("state::callback::IN_PROGRESS", resIdSuccess))
        .as("State IN_PROGRESS is not del")
        .isFalse();
    user = usersDAO.findById(params200Hash.getUserId()).orElse(null);
    assertThat(user).as("Users is not null DB").isNotNull();
    assertThat(user.getId().toString()).as("User ID DB").isEqualTo(userIdOnline);
    assertThat(user.getUsername()).as("User username DB").isEqualTo("testUserOnline");
    assertThat(user.getStatus()).as("User status is not null DB").isNotNull();
    assertThat(user.getStatus().getId()).as("User status ID DB").isEqualTo(statusId);
    assertThat(user.getStatus().getName()).as("User status name DB").isEqualTo("testName");
  }
}
