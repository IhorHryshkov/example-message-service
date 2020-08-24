package unit.com.example.ems.network.controllers;

import com.example.ems.services.CallbackService;
import com.example.ems.utils.network.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CallbackControllerTest {
  @Mock private CallbackService callbackService;
  @Mock private Response<Object> response;

  @Test
  void add() {}
}
