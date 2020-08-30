package integration.com.example.ems;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ems.EmsApplication;
import com.example.ems.database.dao.redis.StateDAO;
import com.example.ems.dto.network.controller.Callback;
import com.example.ems.dto.network.controller.Res;
import com.example.ems.dto.network.controller.ResError;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(classes = EmsApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class CallbackControllerTest {
  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private RedisTemplate<Object, Object> redisTemplate;
  @Autowired private StateDAO stateDAO;

  private final ObjectMapper mapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushAll();
    stateDAO.add(
        "state::callback::IN_PROGRESS", "b8b8794d-e4a4-4614-9d2a-541835ce4ce9", "testData");
  }

  @Test
  public void add() {
    String endpointExpected = "/v1/callback/approve";
    Callback callbackEmpty = new Callback();
    Callback callbackSuccess = new Callback("b8b8794d-e4a4-4614-9d2a-541835ce4ce9");
    ResponseEntity<Res> responseEntity =
        this.restTemplate.postForEntity(createURLWithPort(endpointExpected), null, Res.class);
    assertThat(responseEntity.getStatusCodeValue()).as("Status code is incorrect").isEqualTo(500);
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
    assertThat(resError.getCode()).as("Error data code is incorrect").isEqualTo(500);
    assertThat(resError.getMethod()).as("Error data method is incorrect").isEqualTo("POST");
    assertThat(resError.getEndpoint())
        .as("Error data endpoint is incorrect")
        .isEqualTo(endpointExpected);
    assertThat(resError.getMessage())
        .as("Error data message is incorrect")
        .isEqualTo("Internal server error, please try again later.");

    responseEntity =
        this.restTemplate.postForEntity(
            createURLWithPort(endpointExpected), callbackEmpty, Res.class);
    assertThat(responseEntity.getStatusCodeValue()).as("Status code is incorrect").isEqualTo(422);
    assertThat(responseEntity.getHeaders().getETag()).as("Etag is null").isNull();
    assertThat(responseEntity.getHeaders().getContentType())
        .as("Content type is JSON")
        .isEqualTo(MediaType.APPLICATION_JSON);
    assertThat(responseEntity.getBody())
        .as("Body is not null and is Res class")
        .isNotNull()
        .isInstanceOf(Res.class);
    resBody = (Res<?>) responseEntity.getBody();
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
    resError = resBody.getError();
    assertThat(resError.getCode()).as("Error data code is incorrect").isEqualTo(422);
    assertThat(resError.getMethod()).as("Error data method is incorrect").isEqualTo("POST");
    assertThat(resError.getEndpoint())
        .as("Error data endpoint is incorrect")
        .isEqualTo(endpointExpected);
    assertThat(resError.getMessage())
        .as("Error data message is incorrect")
        .isEqualTo("Request body or query or path params data is incorrect");

    responseEntity =
        this.restTemplate.postForEntity(
            createURLWithPort(endpointExpected), callbackSuccess, Res.class);
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
    resBody = (Res<?>) responseEntity.getBody();
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
        .as("Data is not null and Callback class")
        .isNotNull()
        .isInstanceOf(Object.class);
    Callback resData = mapper.convertValue(resBody.getData(), Callback.class);
    assertThat(resData.getResId())
        .as("Data res ID is incorrect")
        .isEqualTo("b8b8794d-e4a4-4614-9d2a-541835ce4ce9");
    assertThat(
            redisTemplate
                .opsForHash()
                .hasKey("state::callback::RESOLVE", "b8b8794d-e4a4-4614-9d2a-541835ce4ce9"))
        .as("State RESOLVE is not add")
        .isTrue();
    assertThat(
            redisTemplate
                .opsForHash()
                .hasKey("state::callback::RESOLVE::expire", "b8b8794d-e4a4-4614-9d2a-541835ce4ce9"))
        .as("State RESOLVE expire is not add")
        .isTrue();
    assertThat(
            redisTemplate
                .opsForHash()
                .hasKey("state::callback::IN_PROGRESS", "b8b8794d-e4a4-4614-9d2a-541835ce4ce9"))
        .as("State IN_PROGRESS is not del")
        .isFalse();
  }

  private String createURLWithPort(String uri) {
    return String.format("http://localhost:%d%s", port, uri);
  }
}
