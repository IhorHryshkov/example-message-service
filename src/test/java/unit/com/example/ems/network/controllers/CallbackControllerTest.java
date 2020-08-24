package unit.com.example.ems.network.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.example.ems.dto.network.controller.Callback;
import com.example.ems.dto.network.controller.Res;
import com.example.ems.network.controllers.CallbackController;
import com.example.ems.services.CallbackService;
import com.example.ems.utils.network.Response;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CallbackControllerTest {
  @Mock private CallbackService callbackService;
  @Mock private Response<Object> response;

  @InjectMocks private CallbackController callbackController;
  private Long timestamp;
  private String uuid;

  @BeforeEach
  void setUp() {
    timestamp = Instant.now().toEpochMilli();
    uuid = UUID.randomUUID().toString();
  }

  @Test
  void add() {
    Callback params = new Callback("88239958-fdb5-442a-9493-9797c3ab8736");
    doNothing().when(callbackService).removeState(eq("88239958-fdb5-442a-9493-9797c3ab8736"));
    when(response.formattedSuccess(
            eq(params), eq(MediaType.APPLICATION_JSON), eq(HttpStatus.OK.value()), eq("")))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(
            ResponseEntity.status(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON)
                .eTag("")
                .body(new Res<>(uuid, params, null, timestamp)));
    assertThat(catchThrowable(() -> callbackController.add(params)))
        .as("Check a some error")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    ResponseEntity<Res<Object>> add = callbackController.add(params);
    assertThat(add.getStatusCode()).as("Status code").isEqualTo(HttpStatus.OK);
    assertThat(add.getBody()).as("Body not null").isNotNull();
    Res<Object> res = add.getBody();
    assertThat(res.getError()).as("Error is null").isNull();
    assertThat(res.getTimestamp()).as("Timestamp").isEqualTo(timestamp);
    assertThat(res.getResId()).as("Res ID").isEqualTo(uuid);
    assertThat(res.getData()).as("Data is not null").isNotNull();
    Callback body = (Callback) res.getData();
    assertThat(body.getResId()).as("Res ID").isEqualTo("88239958-fdb5-442a-9493-9797c3ab8736");
  }
}
