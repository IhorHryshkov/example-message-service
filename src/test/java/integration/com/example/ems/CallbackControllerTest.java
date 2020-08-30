package integration.com.example.ems;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ems.EmsApplication;
import com.example.ems.dto.network.controller.Callback;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;

@SpringBootTest(classes = EmsApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class CallbackControllerTest {
  @Autowired private TestRestTemplate restTemplate;
  @Autowired private RedisTemplate<Object, Object> redisTemplate;

  @Test
  public void add() {
    Callback callback = new Callback("b8b8794d-e4a4-4614-9d2a-541835ce4ce9");
    ResponseEntity<Object> responseEntity =
        this.restTemplate.postForEntity(
            "http://localhost:31111/v1/callback/approve", callback, Object.class);
    assertThat(responseEntity.getStatusCodeValue()).as("Status code").isEqualTo(200);
  }

  private String createURLWithPort(String uri) {
    return "http://localhost:31111" + uri;
  }
}
