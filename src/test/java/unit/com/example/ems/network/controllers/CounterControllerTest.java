package unit.com.example.ems.network.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.example.ems.dto.database.pg.Counters;
import com.example.ems.dto.network.controller.Res;
import com.example.ems.dto.network.controller.State;
import com.example.ems.dto.network.controller.counter.AddIn;
import com.example.ems.dto.network.controller.counter.GetByIdIn;
import com.example.ems.dto.network.controller.counter.GetByIdOut;
import com.example.ems.network.controllers.CounterController;
import com.example.ems.network.controllers.exceptions.global.ResponseEmptyException;
import com.example.ems.network.controllers.exceptions.global.ResponseIfNoneMatchException;
import com.example.ems.services.CacheService;
import com.example.ems.services.CounterService;
import com.example.ems.utils.enums.States;
import com.example.ems.utils.network.Response;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CounterControllerTest {
  @Mock private CounterService counterService;
  @Mock private CacheService cacheService;
  @Mock private Response<Object> response;

  @InjectMocks private CounterController counterController;

  private Long timestamp;
  private String uuid;

  @BeforeEach
  void setUp() {
    timestamp = Instant.now().toEpochMilli();
    uuid = UUID.randomUUID().toString();
    MDC.put("resId", uuid);
  }

  @Test
  void getById() {
    MDC.put("fullPathQuery", "/v1/test");
    MDC.put("ifNoneMatch", "testHash");

    GetByIdIn in = new GetByIdIn();
    in.setUserId("88239958-fdb5-442a-9493-9797c3ab8736");

    String key =
        "counterCache::getById::forMatch::3496fdbcd7ecef849bec992d9441d86fe8cba183882421327c37a9ed45e70b7d";
    doThrow(new ResponseIfNoneMatchException())
        .doNothing()
        .when(cacheService)
        .hexistOrIfNoneMatch(eq(key), eq("testHash"));
    List<Counters> counter = Collections.singletonList(new Counters());
    GetByIdOut<List<Counters>> out = new GetByIdOut<>();
    out.setData(counter);
    out.setEtag("testEtag");
    when(counterService.getByUserId(eq(in))).thenReturn(new GetByIdOut<>()).thenReturn(out);
    doNothing().when(cacheService).hset(eq(key), eq("testEtag"), eq(""));
    when(response.formattedSuccess(
            eq(counter), eq(MediaType.APPLICATION_JSON), eq(HttpStatus.OK.value()), eq("testEtag")))
        .thenReturn(
            ResponseEntity.status(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON)
                .eTag("testEtag")
                .body(new Res<>(uuid, counter, null, timestamp)));
    assertThat(catchThrowable(() -> counterController.getById(in)))
        .as("Check if none match")
        .isInstanceOf(ResponseIfNoneMatchException.class);
    assertThat(catchThrowable(() -> counterController.getById(in)))
        .as("Check if response empty")
        .isInstanceOf(ResponseEmptyException.class);
    ResponseEntity<Res<Object>> getById = counterController.getById(in);
    assertThat(getById.getStatusCode()).as("Status code").isEqualTo(HttpStatus.OK);
    assertThat(getById.getBody()).as("Body not null").isNotNull();
    Res<Object> res = getById.getBody();
    assertThat(res.getError()).as("Error is null").isNull();
    assertThat(res.getTimestamp()).as("Timestamp").isEqualTo(timestamp);
    assertThat(res.getResId()).as("Res ID").isEqualTo(uuid);
    assertThat(res.getData()).as("Data is not null").isNotNull();
    assertThat(res.getData()).as("Data is list").isEqualTo(counter);
  }

  @Test
  void add() {
    AddIn params = new AddIn();
    params.setUserId("88239958-fdb5-442a-9493-9797c3ab8736");
    params.setTypeId(1);
    params.setCount(1L);
    AddIn addSer = new AddIn();
    addSer.setUserId("88239958-fdb5-442a-9493-9797c3ab8736");
    addSer.setTypeId(1);
    addSer.setCount(1L);
    addSer.setResId(uuid);
    State state = new State(States.IN_PROGRESS.toString());
    when(counterService.add(eq(addSer))).thenReturn(States.IN_PROGRESS, States.RESOLVE);
    when(response.formattedSuccess(
            eq(state), eq(MediaType.APPLICATION_JSON), eq(HttpStatus.ACCEPTED.value()), eq("")))
        .thenReturn(
            ResponseEntity.status(HttpStatus.ACCEPTED.value())
                .contentType(MediaType.APPLICATION_JSON)
                .eTag("")
                .body(new Res<>(uuid, state, null, timestamp)));
    when(response.formattedSuccess(
            eq(params), eq(MediaType.APPLICATION_JSON), eq(HttpStatus.CREATED.value()), eq("")))
        .thenReturn(
            ResponseEntity.status(HttpStatus.CREATED.value())
                .contentType(MediaType.APPLICATION_JSON)
                .eTag("")
                .body(new Res<>(uuid, params, null, timestamp)));
    ResponseEntity<Res<Object>> add = counterController.add(params);
    assertThat(add.getStatusCode()).as("Status code state").isEqualTo(HttpStatus.ACCEPTED);
    assertThat(add.getBody()).as("Body not null state").isNotNull();
    Res<Object> res = add.getBody();
    assertThat(res.getError()).as("Error is null state").isNull();
    assertThat(res.getTimestamp()).as("Timestamp state").isEqualTo(timestamp);
    assertThat(res.getResId()).as("Res ID state").isEqualTo(uuid);
    assertThat(res.getData()).as("Data is not null state").isNotNull();
    State bodyState = (State) res.getData();
    assertThat(bodyState.getState()).as("State name").isEqualTo(States.IN_PROGRESS.toString());

    add = counterController.add(params);
    assertThat(add.getStatusCode()).as("Status code result").isEqualTo(HttpStatus.CREATED);
    assertThat(add.getBody()).as("Body not null result").isNotNull();
    res = add.getBody();
    assertThat(res.getError()).as("Error is null result").isNull();
    assertThat(res.getTimestamp()).as("Timestamp result").isEqualTo(timestamp);
    assertThat(res.getResId()).as("Res ID result").isEqualTo(uuid);
    assertThat(res.getData()).as("Data is not null result").isNotNull();
    assertThat(res.getData()).as("Body data").isEqualTo(params);
  }
}
