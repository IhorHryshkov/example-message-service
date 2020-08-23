package unit.com.example.ems.network.controllers.advice.global;

import com.example.ems.config.messages.Messages;
import com.example.ems.dto.network.controller.Message;
import com.example.ems.dto.network.controller.Res;
import com.example.ems.dto.network.controller.ResError;
import com.example.ems.network.controllers.advice.global.GlobalServiceErrorAdvice;
import com.example.ems.utils.network.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalServiceErrorAdviceTest {
  @Mock private Response<Object> response;
  @Mock private Messages messages;
  @Mock private Message message;
  @Mock private HttpServletRequest req;
  @Mock private Exception ex;

  @InjectMocks private GlobalServiceErrorAdvice globalServiceErrorAdvice;

  @Test
  void handleAnyException() {
    when(message.getMessage()).thenReturn("Error message");
    when(message.getCode()).thenReturn(500);
    when(messages.getInternalServerError()).thenReturn(message);
    when(ex.getMessage()).thenReturn("Some error");
    when(response.formattedError(
            eq(req), eq("Error message"), eq(MediaType.APPLICATION_JSON), eq(500)))
        .thenReturn(
            ResponseEntity.status(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    new Res<>(null, null, new ResError(500, "Error message", "GET", null), null)));

    ResponseEntity<Res<Object>> responseEntity =
        globalServiceErrorAdvice.handleAnyException(req, ex);
    assertThat(responseEntity.getStatusCode())
        .as("Status code")
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  void handleValidationException() {}

  @Test
  void handleResourceNotFoundException() {}

  @Test
  void handleResultEmpty() {}

  @Test
  void handleNotModified() {}

  @Test
  void handleUsernameUsed() {}

  @Test
  void handleUserIDNotfound() {}

  @Test
  void handleBindException() {}
}
