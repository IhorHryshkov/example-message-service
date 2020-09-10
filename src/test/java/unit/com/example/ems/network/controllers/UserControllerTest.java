/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-13T09:35
 */
package unit.com.example.ems.network.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.example.ems.dto.database.pg.Users;
import com.example.ems.dto.network.controller.Res;
import com.example.ems.dto.network.controller.State;
import com.example.ems.dto.network.controller.user.AddIn;
import com.example.ems.dto.network.controller.user.AllIn;
import com.example.ems.dto.network.controller.user.AllOut;
import com.example.ems.dto.network.controller.user.UpdateIn;
import com.example.ems.network.controllers.UserController;
import com.example.ems.network.controllers.exceptions.global.ResponseEmptyException;
import com.example.ems.network.controllers.exceptions.global.ResponseIfNoneMatchException;
import com.example.ems.services.CacheService;
import com.example.ems.services.UserService;
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
class UserControllerTest {
  @Mock private UserService userService;
  @Mock private Response<Object> response;
  @Mock private CacheService cacheService;

  @InjectMocks private UserController userController;

  private Long timestamp;
  private String uuid;

  @BeforeEach
  void setUp() {
    timestamp = Instant.now().toEpochMilli();
    uuid = UUID.randomUUID().toString();
    MDC.put("resId", uuid);
  }

  @Test
  void all() {
    MDC.put("fullPathQuery", "/v1/test");
    MDC.put("ifNoneMatch", "testHash");

    AllIn in = new AllIn();
    in.setUserId("99d4160b-d8e4-4425-a856-b4f2285f9ad5");
    in.setUsername("tester123");
    AllIn inExpected = new AllIn();
    inExpected.setUserId("99d4160b-d8e4-4425-a856-b4f2285f9ad5");
    inExpected.setUsername("tester123");
    inExpected.setPath("/v1/test");
    inExpected.setResId(uuid);

    String noneMatchKeyExpected = "userCache::all::forMatch::testHash";
    String etagKeyExpected = "userCache::all::forMatch::testEtag";
    doThrow(new ResponseIfNoneMatchException())
        .doNothing()
        .when(cacheService)
        .existOrIfNoneMatch(eq(noneMatchKeyExpected));
    List<Users> typesList = Collections.singletonList(new Users());
    List<Users> typesListExpected = Collections.singletonList(new Users());
    AllOut<Users> out = new AllOut<>();
    out.setData(typesList);
    out.setEtag("testEtag");
    when(userService.all(eq(inExpected))).thenReturn(new AllOut<>()).thenReturn(out);
    doNothing().when(cacheService).setKeyForCheckWithTtlDivider(eq(etagKeyExpected), eq(2));
    when(response.formattedSuccess(
            eq(typesListExpected),
            eq(MediaType.APPLICATION_JSON),
            eq(HttpStatus.OK.value()),
            eq("testEtag")))
        .thenReturn(
            ResponseEntity.status(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON)
                .eTag("testEtag")
                .body(new Res(uuid, typesList, null, timestamp)));
    assertThat(catchThrowable(() -> userController.all(in)))
        .as("Check if none match")
        .isInstanceOf(ResponseIfNoneMatchException.class);
    assertThat(catchThrowable(() -> userController.all(in)))
        .as("Check if response empty")
        .isInstanceOf(ResponseEmptyException.class);
    ResponseEntity<Res> all = userController.all(in);
    assertThat(all.getStatusCode()).as("Status code").isEqualTo(HttpStatus.OK);
    assertThat(all.getBody()).as("Body not null").isNotNull();
    Res res = all.getBody();
    assertThat(res.getError()).as("Error is null").isNull();
    assertThat(res.getTimestamp()).as("Timestamp").isEqualTo(timestamp);
    assertThat(res.getResId()).as("Res ID").isEqualTo(uuid);
    assertThat(res.getData()).as("Data is not null").isNotNull();
    assertThat(res.getData()).as("Data is list").isEqualTo(typesList);
  }

  @Test
  void update() {
    String userId = "88239958-fdb5-442a-9493-9797c3ab8736";
    String userIdExpected = "88239958-fdb5-442a-9493-9797c3ab8736";
    UpdateIn bodyIn = new UpdateIn();
    bodyIn.setStatusId(1);
    UpdateIn bodyResolve = new UpdateIn();
    bodyResolve.setStatusId(1);
    bodyResolve.setUserId(userIdExpected);
    UpdateIn fullBodyInit = new UpdateIn();
    fullBodyInit.setUserId(userIdExpected);
    fullBodyInit.setStatusId(1);
    fullBodyInit.setResId(uuid);
    State state = new State(States.IN_PROGRESS.toString());
    State stateExpected = new State(States.IN_PROGRESS.toString());
    when(userService.updateCounterAndStatus(eq(fullBodyInit)))
        .thenReturn(States.IN_PROGRESS, States.RESOLVE);
    when(response.formattedSuccess(
            eq(stateExpected),
            eq(MediaType.APPLICATION_JSON),
            eq(HttpStatus.ACCEPTED.value()),
            eq("")))
        .thenReturn(
            ResponseEntity.status(HttpStatus.ACCEPTED.value())
                .contentType(MediaType.APPLICATION_JSON)
                .eTag("")
                .body(new Res(uuid, state, null, timestamp)));
    when(response.formattedSuccess(
            eq(bodyResolve), eq(MediaType.APPLICATION_JSON), eq(HttpStatus.OK.value()), eq("")))
        .thenReturn(
            ResponseEntity.status(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON)
                .eTag("")
                .body(new Res(uuid, bodyResolve, null, timestamp)));
    ResponseEntity<Res> update = userController.update(userId, bodyIn);
    assertThat(update.getStatusCode()).as("Status code state").isEqualTo(HttpStatus.ACCEPTED);
    assertThat(update.getBody()).as("Body not null state").isNotNull();
    Res res = update.getBody();
    assertThat(res.getError()).as("Error is null state").isNull();
    assertThat(res.getTimestamp()).as("Timestamp state").isEqualTo(timestamp);
    assertThat(res.getResId()).as("Res ID state").isEqualTo(uuid);
    assertThat(res.getData()).as("Data is not null state").isNotNull();
    State bodyState = (State) res.getData();
    assertThat(bodyState.getState()).as("State name").isEqualTo(States.IN_PROGRESS.toString());

    update = userController.update(userId, bodyIn);
    assertThat(update.getStatusCode()).as("Status code result").isEqualTo(HttpStatus.OK);
    assertThat(update.getBody()).as("Body not null result").isNotNull();
    res = update.getBody();
    assertThat(res.getError()).as("Error is null result").isNull();
    assertThat(res.getTimestamp()).as("Timestamp result").isEqualTo(timestamp);
    assertThat(res.getResId()).as("Res ID result").isEqualTo(uuid);
    assertThat(res.getData()).as("Data is not null result").isNotNull();
    assertThat(res.getData()).as("Body data").isEqualTo(bodyResolve);
  }

  @Test
  void add() {
    AddIn params = new AddIn();
    params.setUsername("tester");
    AddIn paramsExpected = new AddIn();
    paramsExpected.setUsername("tester");
    AddIn fullParams = new AddIn();
    fullParams.setUsername("tester");
    fullParams.setResId(uuid);
    AddIn fullParamsExpected = new AddIn();
    fullParamsExpected.setUsername("tester");
    fullParamsExpected.setResId(uuid);
    State state = new State(States.IN_PROGRESS.toString());
    State stateExpected = new State(States.IN_PROGRESS.toString());
    when(userService.add(eq(fullParamsExpected))).thenReturn(States.IN_PROGRESS, States.RESOLVE);
    when(response.formattedSuccess(
            eq(stateExpected),
            eq(MediaType.APPLICATION_JSON),
            eq(HttpStatus.ACCEPTED.value()),
            eq("")))
        .thenReturn(
            ResponseEntity.status(HttpStatus.ACCEPTED.value())
                .contentType(MediaType.APPLICATION_JSON)
                .eTag("")
                .body(new Res(uuid, state, null, timestamp)));
    when(response.formattedSuccess(
            eq(paramsExpected),
            eq(MediaType.APPLICATION_JSON),
            eq(HttpStatus.CREATED.value()),
            eq("")))
        .thenReturn(
            ResponseEntity.status(HttpStatus.CREATED.value())
                .contentType(MediaType.APPLICATION_JSON)
                .eTag("")
                .body(new Res(uuid, params, null, timestamp)));
    ResponseEntity<Res> add = userController.add(params);
    assertThat(add.getStatusCode()).as("Status code state").isEqualTo(HttpStatus.ACCEPTED);
    assertThat(add.getBody()).as("Body not null state").isNotNull();
    Res res = add.getBody();
    assertThat(res.getError()).as("Error is null state").isNull();
    assertThat(res.getTimestamp()).as("Timestamp state").isEqualTo(timestamp);
    assertThat(res.getResId()).as("Res ID state").isEqualTo(uuid);
    assertThat(res.getData()).as("Data is not null state").isNotNull();
    State bodyState = (State) res.getData();
    assertThat(bodyState.getState()).as("State name").isEqualTo(States.IN_PROGRESS.toString());

    add = userController.add(params);
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
