/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-30T08:45
 */
package unit.com.example.ems.utils.network;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.ems.dto.network.controller.Res;
import com.example.ems.dto.network.controller.ResError;
import com.example.ems.utils.network.Response;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ResponseTest {
  @Mock private HttpServletRequest req;

  @InjectMocks private Response<String> response;

  @BeforeEach
  void setUp() {
    MDC.put("resId", "testResId");
  }

  @Test
  void formattedSuccess() {
    String data = "testData";
    String dataExpected = "testData";
    MediaType type = MediaType.APPLICATION_JSON;
    MediaType typeExpected = MediaType.APPLICATION_JSON;
    Integer status = 200;
    Integer statusExpected = 200;
    String etag = "testEtag";
    String etagExpected = "\"testEtag\"";
    ResponseEntity<Res> responseEntity = response.formattedSuccess(data, type, status, etag);
    assertThat(responseEntity.getStatusCodeValue()).as("Status code").isEqualTo(statusExpected);
    assertThat(responseEntity.getBody()).as("Body is not null").isNotNull();
    assertThat(responseEntity.getBody()).as("Body is Res class").isInstanceOf(Res.class);
    assertThat(responseEntity.getHeaders().getContentType())
        .as("Content type")
        .isEqualTo(typeExpected);
    assertThat(responseEntity.getHeaders().getETag()).as("Etag").isEqualTo(etagExpected);
    Res res = responseEntity.getBody();
    assertThat(res.getError()).as("Error is null").isNull();
    assertThat(res.getResId()).as("Res ID").isEqualTo("testResId");
    assertThat(res.getTimestamp()).as("Timestamp is not null").isNotNull();
    assertThat(res.getData()).as("Body data").isEqualTo(dataExpected);
  }

  @Test
  void formattedError() {
    MDC.put("fullPathQuery", "testPathQuery");
    String message = "testMessage";
    String messageExpected = "testMessage";
    MediaType type = MediaType.APPLICATION_JSON;
    MediaType typeExpected = MediaType.APPLICATION_JSON;
    Integer status = 400;
    Integer statusExpected = 400;
    when(req.getMethod()).thenReturn("GET");
    ResponseEntity<Res> responseEntity = response.formattedError(req, message, type, status);
    assertThat(responseEntity.getStatusCodeValue()).as("Status code").isEqualTo(statusExpected);
    assertThat(responseEntity.getBody()).as("Body is not null").isNotNull();
    assertThat(responseEntity.getBody()).as("Body is Res class").isInstanceOf(Res.class);
    assertThat(responseEntity.getHeaders().getContentType())
        .as("Content type")
        .isEqualTo(typeExpected);
    Res res = responseEntity.getBody();
    assertThat(res.getError()).as("Error is not null").isNotNull();
    assertThat(res.getResId()).as("Res ID").isEqualTo("testResId");
    assertThat(res.getTimestamp()).as("Timestamp is not null").isNotNull();
    assertThat(res.getData()).as("Data is null").isNull();
    ResError error = res.getError();
    assertThat(error.getEndpoint()).as("Error endpoint").isEqualTo("testPathQuery");
    assertThat(error.getMessage()).as("Error message").isEqualTo(messageExpected);
    assertThat(error.getMethod()).as("Error method").isEqualTo("GET");
    assertThat(error.getCode()).as("Error code").isEqualTo(statusExpected);
    // If status is null return code and message 500
    responseEntity = response.formattedError(req, message, type, null);
    assertThat(responseEntity.getStatusCodeValue()).as("Status code").isEqualTo(500);
    assertThat(responseEntity.getBody()).as("Body is not null").isNotNull();
    assertThat(responseEntity.getBody()).as("Body is Res class").isInstanceOf(Res.class);
    assertThat(responseEntity.getHeaders().getContentType())
        .as("Content type")
        .isEqualTo(typeExpected);
    res = responseEntity.getBody();
    assertThat(res.getError()).as("Error is not null").isNotNull();
    assertThat(res.getResId()).as("Res ID").isEqualTo("testResId");
    assertThat(res.getTimestamp()).as("Timestamp is not null").isNotNull();
    assertThat(res.getData()).as("Data is null").isNull();
    error = res.getError();
    assertThat(error.getEndpoint()).as("Error endpoint").isEqualTo("testPathQuery");
    assertThat(error.getMessage()).as("Error message").isEqualTo(messageExpected);
    assertThat(error.getMethod()).as("Error method").isEqualTo("GET");
    assertThat(error.getCode()).as("Error code").isEqualTo(500);
  }
}
