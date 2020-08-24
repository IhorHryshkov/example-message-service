package unit.com.example.ems.network.controllers.advice.global;

import com.example.ems.config.messages.Messages;
import com.example.ems.dto.network.controller.Message;
import com.example.ems.dto.network.controller.Res;
import com.example.ems.dto.network.controller.ResError;
import com.example.ems.network.controllers.advice.global.GlobalServiceErrorAdvice;
import com.example.ems.utils.network.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;

import javax.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalServiceErrorAdviceTest {
  @Mock private Response<Object> response;
  @Mock private Messages messages;
  @Mock private Message message;
  @Mock private HttpServletRequest req;
  @Mock private Exception ex;
  @Mock private BindException bEx;

  @InjectMocks private GlobalServiceErrorAdvice globalServiceErrorAdvice;

  private Long timestamp;
  private String uuid;
  private String endpoint;
  private String errMessage;
  private String method;

  @BeforeEach
  void setUp() {
    timestamp = Instant.now().toEpochMilli();
    uuid = UUID.randomUUID().toString();
    endpoint = "/v1/test";
    errMessage = "Error message";
    method = "GET";
  }

  @Test
  void handleAnyException() {
    when(message.getMessage()).thenReturn(errMessage);
    when(message.getCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());
    when(messages.getInternalServerError()).thenReturn(message);
    when(ex.getMessage()).thenReturn("Some error");
    when(response.formattedError(
            eq(req),
            eq(errMessage),
            eq(MediaType.APPLICATION_JSON),
            eq(HttpStatus.INTERNAL_SERVER_ERROR.value())))
        .thenReturn(
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    new Res<>(
                        uuid,
                        null,
                        new ResError(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(), errMessage, method, endpoint),
                        timestamp)));

    ResponseEntity<Res<Object>> responseEntity =
        globalServiceErrorAdvice.handleAnyException(req, ex);
    assertThat(responseEntity.getStatusCode())
        .as("Status code")
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(responseEntity.getBody()).as("Body not null").isNotNull();
    Res<Object> res = responseEntity.getBody();
    assertThat(res.getData()).as("Data is null").isNull();
    assertThat(res.getTimestamp()).as("Timestamp").isEqualTo(timestamp);
    assertThat(res.getResId()).as("Res ID").isEqualTo(uuid);
    assertThat(res.getError()).as("Error data is not null").isNotNull();
    ResError resError = res.getError();
    assertThat(resError.getCode())
        .as("Error data code")
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(resError.getMethod()).as("Error data method").isEqualTo(method);
    assertThat(resError.getMessage()).as("Error data message").isEqualTo(errMessage);
    assertThat(resError.getEndpoint()).as("Error data endpoint").isEqualTo(endpoint);
  }

  @Test
  void handleValidationException() {
    when(message.getMessage()).thenReturn(errMessage);
    when(message.getCode()).thenReturn(HttpStatus.UNPROCESSABLE_ENTITY.value());
    when(messages.getRequestBodyIncorrect()).thenReturn(message);
    when(ex.getMessage()).thenReturn("Some error");
    when(response.formattedError(
            eq(req),
            eq(errMessage),
            eq(MediaType.APPLICATION_JSON),
            eq(HttpStatus.UNPROCESSABLE_ENTITY.value())))
        .thenReturn(
            ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    new Res<>(
                        uuid,
                        null,
                        new ResError(
                            HttpStatus.UNPROCESSABLE_ENTITY.value(), errMessage, method, endpoint),
                        timestamp)));

    ResponseEntity<Res<Object>> responseEntity =
        globalServiceErrorAdvice.handleValidationException(req, ex);
    assertThat(responseEntity.getStatusCode())
        .as("Status code")
        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(responseEntity.getBody()).as("Body not null").isNotNull();
    Res<Object> res = responseEntity.getBody();
    assertThat(res.getData()).as("Data is null").isNull();
    assertThat(res.getTimestamp()).as("Timestamp").isEqualTo(timestamp);
    assertThat(res.getResId()).as("Res ID").isEqualTo(uuid);
    assertThat(res.getError()).as("Error data is not null").isNotNull();
    ResError resError = res.getError();
    assertThat(resError.getCode())
        .as("Error data code")
        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    assertThat(resError.getMethod()).as("Error data method").isEqualTo(method);
    assertThat(resError.getMessage()).as("Error data message").isEqualTo(errMessage);
    assertThat(resError.getEndpoint()).as("Error data endpoint").isEqualTo(endpoint);
  }

  @Test
  void handleResourceNotFoundException() {
    when(message.getMessage()).thenReturn(errMessage);
    when(message.getCode()).thenReturn(HttpStatus.NOT_FOUND.value());
    when(messages.getEndpointNotFound()).thenReturn(message);
    when(ex.getMessage()).thenReturn("Some error");
    when(response.formattedError(
            eq(req),
            eq(errMessage),
            eq(MediaType.APPLICATION_JSON),
            eq(HttpStatus.NOT_FOUND.value())))
        .thenReturn(
            ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    new Res<>(
                        uuid,
                        null,
                        new ResError(HttpStatus.NOT_FOUND.value(), errMessage, method, endpoint),
                        timestamp)));

    ResponseEntity<Res<Object>> responseEntity =
        globalServiceErrorAdvice.handleResourceNotFoundException(req, ex);
    assertThat(responseEntity.getStatusCode()).as("Status code").isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(responseEntity.getBody()).as("Body not null").isNotNull();
    Res<Object> res = responseEntity.getBody();
    assertThat(res.getData()).as("Data is null").isNull();
    assertThat(res.getTimestamp()).as("Timestamp").isEqualTo(timestamp);
    assertThat(res.getResId()).as("Res ID").isEqualTo(uuid);
    assertThat(res.getError()).as("Error data is not null").isNotNull();
    ResError resError = res.getError();
    assertThat(resError.getCode()).as("Error data code").isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(resError.getMethod()).as("Error data method").isEqualTo(method);
    assertThat(resError.getMessage()).as("Error data message").isEqualTo(errMessage);
    assertThat(resError.getEndpoint()).as("Error data endpoint").isEqualTo(endpoint);
  }

  @Test
  void handleResultEmpty() {
    when(message.getCode()).thenReturn(HttpStatus.NO_CONTENT.value());
    when(messages.getResultEmpty()).thenReturn(message);
    when(ex.getMessage()).thenReturn("Some error");
    when(response.formattedError(
            eq(req), isNull(), eq(MediaType.APPLICATION_JSON), eq(HttpStatus.NO_CONTENT.value())))
        .thenReturn(
            ResponseEntity.status(HttpStatus.NO_CONTENT.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    new Res<>(
                        uuid,
                        null,
                        new ResError(HttpStatus.NO_CONTENT.value(), errMessage, method, endpoint),
                        timestamp)));

    ResponseEntity<Res<Object>> responseEntity =
        globalServiceErrorAdvice.handleResultEmpty(req, ex);
    assertThat(responseEntity.getStatusCode()).as("Status code").isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(responseEntity.getBody()).as("Body not null").isNotNull();
    Res<Object> res = responseEntity.getBody();
    assertThat(res.getData()).as("Data is null").isNull();
    assertThat(res.getTimestamp()).as("Timestamp").isEqualTo(timestamp);
    assertThat(res.getResId()).as("Res ID").isEqualTo(uuid);
    assertThat(res.getError()).as("Error data is not null").isNotNull();
    ResError resError = res.getError();
    assertThat(resError.getCode()).as("Error data code").isEqualTo(HttpStatus.NO_CONTENT.value());
    assertThat(resError.getMethod()).as("Error data method").isEqualTo(method);
    assertThat(resError.getMessage()).as("Error data message").isEqualTo(errMessage);
    assertThat(resError.getEndpoint()).as("Error data endpoint").isEqualTo(endpoint);
  }

  @Test
  void handleNotModified() {
    when(message.getCode()).thenReturn(HttpStatus.NOT_MODIFIED.value());
    when(messages.getNotModified()).thenReturn(message);
    when(ex.getMessage()).thenReturn("Some error");
    when(response.formattedError(
            eq(req), isNull(), eq(MediaType.APPLICATION_JSON), eq(HttpStatus.NOT_MODIFIED.value())))
        .thenReturn(
            ResponseEntity.status(HttpStatus.NOT_MODIFIED.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    new Res<>(
                        uuid,
                        null,
                        new ResError(HttpStatus.NOT_MODIFIED.value(), errMessage, method, endpoint),
                        timestamp)));

    ResponseEntity<Res<Object>> responseEntity =
        globalServiceErrorAdvice.handleNotModified(req, ex);
    assertThat(responseEntity.getStatusCode()).as("Status code").isEqualTo(HttpStatus.NOT_MODIFIED);
    assertThat(responseEntity.getBody()).as("Body not null").isNotNull();
    Res<Object> res = responseEntity.getBody();
    assertThat(res.getData()).as("Data is null").isNull();
    assertThat(res.getTimestamp()).as("Timestamp").isEqualTo(timestamp);
    assertThat(res.getResId()).as("Res ID").isEqualTo(uuid);
    assertThat(res.getError()).as("Error data is not null").isNotNull();
    ResError resError = res.getError();
    assertThat(resError.getCode()).as("Error data code").isEqualTo(HttpStatus.NOT_MODIFIED.value());
    assertThat(resError.getMethod()).as("Error data method").isEqualTo(method);
    assertThat(resError.getMessage()).as("Error data message").isEqualTo(errMessage);
    assertThat(resError.getEndpoint()).as("Error data endpoint").isEqualTo(endpoint);
  }

  @Test
  void handleUsernameUsed() {
    when(message.getMessage()).thenReturn(errMessage);
    when(message.getCode()).thenReturn(HttpStatus.BAD_REQUEST.value());
    when(messages.getUsernameUsed()).thenReturn(message);
    when(ex.getMessage()).thenReturn("Some error");
    when(response.formattedError(
            eq(req),
            eq(errMessage),
            eq(MediaType.APPLICATION_JSON),
            eq(HttpStatus.BAD_REQUEST.value())))
        .thenReturn(
            ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    new Res<>(
                        uuid,
                        null,
                        new ResError(HttpStatus.BAD_REQUEST.value(), errMessage, method, endpoint),
                        timestamp)));

    ResponseEntity<Res<Object>> responseEntity =
        globalServiceErrorAdvice.handleUsernameUsed(req, ex);
    assertThat(responseEntity.getStatusCode()).as("Status code").isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(responseEntity.getBody()).as("Body not null").isNotNull();
    Res<Object> res = responseEntity.getBody();
    assertThat(res.getData()).as("Data is null").isNull();
    assertThat(res.getTimestamp()).as("Timestamp").isEqualTo(timestamp);
    assertThat(res.getResId()).as("Res ID").isEqualTo(uuid);
    assertThat(res.getError()).as("Error data is not null").isNotNull();
    ResError resError = res.getError();
    assertThat(resError.getCode()).as("Error data code").isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(resError.getMethod()).as("Error data method").isEqualTo(method);
    assertThat(resError.getMessage()).as("Error data message").isEqualTo(errMessage);
    assertThat(resError.getEndpoint()).as("Error data endpoint").isEqualTo(endpoint);
  }

  @Test
  void handleUserIDNotfound() {
    when(message.getMessage()).thenReturn(errMessage);
    when(message.getCode()).thenReturn(HttpStatus.BAD_REQUEST.value());
    when(messages.getUserIdNotFound()).thenReturn(message);
    when(ex.getMessage()).thenReturn("Some error");
    when(response.formattedError(
            eq(req),
            eq(errMessage),
            eq(MediaType.APPLICATION_JSON),
            eq(HttpStatus.BAD_REQUEST.value())))
        .thenReturn(
            ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    new Res<>(
                        uuid,
                        null,
                        new ResError(HttpStatus.BAD_REQUEST.value(), errMessage, method, endpoint),
                        timestamp)));

    ResponseEntity<Res<Object>> responseEntity =
        globalServiceErrorAdvice.handleUserIDNotfound(req, ex);
    assertThat(responseEntity.getStatusCode()).as("Status code").isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(responseEntity.getBody()).as("Body not null").isNotNull();
    Res<Object> res = responseEntity.getBody();
    assertThat(res.getData()).as("Data is null").isNull();
    assertThat(res.getTimestamp()).as("Timestamp").isEqualTo(timestamp);
    assertThat(res.getResId()).as("Res ID").isEqualTo(uuid);
    assertThat(res.getError()).as("Error data is not null").isNotNull();
    ResError resError = res.getError();
    assertThat(resError.getCode()).as("Error data code").isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(resError.getMethod()).as("Error data method").isEqualTo(method);
    assertThat(resError.getMessage()).as("Error data message").isEqualTo(errMessage);
    assertThat(resError.getEndpoint()).as("Error data endpoint").isEqualTo(endpoint);
  }

  @Test
  void handleBindException() {
    when(message.getMessage()).thenReturn(errMessage);
    when(message.getCode()).thenReturn(HttpStatus.UNPROCESSABLE_ENTITY.value());
    when(messages.getRequestBodyIncorrect()).thenReturn(message);
    when(bEx.getMessage()).thenReturn("Some error");
    when(response.formattedError(
            eq(req),
            eq(errMessage),
            eq(MediaType.APPLICATION_JSON),
            eq(HttpStatus.UNPROCESSABLE_ENTITY.value())))
        .thenReturn(
            ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    new Res<>(
                        uuid,
                        null,
                        new ResError(
                            HttpStatus.UNPROCESSABLE_ENTITY.value(), errMessage, method, endpoint),
                        timestamp)));

    ResponseEntity<Res<Object>> responseEntity =
        globalServiceErrorAdvice.handleValidationException(req, bEx);
    assertThat(responseEntity.getStatusCode())
        .as("Status code")
        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(responseEntity.getBody()).as("Body not null").isNotNull();
    Res<Object> res = responseEntity.getBody();
    assertThat(res.getData()).as("Data is null").isNull();
    assertThat(res.getTimestamp()).as("Timestamp").isEqualTo(timestamp);
    assertThat(res.getResId()).as("Res ID").isEqualTo(uuid);
    assertThat(res.getError()).as("Error data is not null").isNotNull();
    ResError resError = res.getError();
    assertThat(resError.getCode())
        .as("Error data code")
        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    assertThat(resError.getMethod()).as("Error data method").isEqualTo(method);
    assertThat(resError.getMessage()).as("Error data message").isEqualTo(errMessage);
    assertThat(resError.getEndpoint()).as("Error data endpoint").isEqualTo(endpoint);
  }
}
