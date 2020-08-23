package unit.com.example.ems.network.controllers.advice.global;

import com.example.ems.config.messages.Messages;
import com.example.ems.dto.network.controller.Message;
import com.example.ems.network.controllers.advice.global.GlobalServiceErrorAdvice;
import com.example.ems.utils.network.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class GlobalServiceErrorAdviceTest {
  @Mock
  private Response<Object>   response;
  @Mock
  private Messages           messages;
  @Mock
  private Message            message;
  @Mock
  private HttpServletRequest req;
  @Mock
  private Exception ex;

  @InjectMocks
  private GlobalServiceErrorAdvice globalServiceErrorAdvice;

  @Test
  void handleAnyException() {
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
