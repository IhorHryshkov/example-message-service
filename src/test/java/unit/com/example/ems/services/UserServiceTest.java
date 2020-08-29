/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-28T22:10
 */
package unit.com.example.ems.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.when;

import com.example.ems.config.rabbitmq.RabbitMQSettings;
import com.example.ems.database.dao.pg.StatusDAO;
import com.example.ems.database.dao.pg.TypesDAO;
import com.example.ems.database.dao.pg.UsersDAO;
import com.example.ems.database.dao.redis.StateDAO;
import com.example.ems.dto.database.pg.Status;
import com.example.ems.dto.database.pg.Types;
import com.example.ems.dto.database.pg.Users;
import com.example.ems.dto.mq.CallbackMQ;
import com.example.ems.dto.mq.QueueConf;
import com.example.ems.dto.mq.StatusMQ;
import com.example.ems.dto.network.controller.user.AddIn;
import com.example.ems.dto.network.controller.user.AddOut;
import com.example.ems.dto.network.controller.user.AllIn;
import com.example.ems.dto.network.controller.user.AllOut;
import com.example.ems.dto.network.controller.user.UpdateIn;
import com.example.ems.dto.network.controller.user.UpdateOut;
import com.example.ems.network.controllers.exceptions.status.UserIDNotFoundException;
import com.example.ems.services.QueueService;
import com.example.ems.services.UserService;
import com.example.ems.services.components.UserCounterComponent;
import com.example.ems.utils.enums.States;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock private UsersDAO usersDAO;
  @Mock private StatusDAO statusDAO;
  @Mock private StateDAO stateDAO;
  @Mock private TypesDAO typesDAO;
  @Mock private QueueService queueService;
  @Mock private UserCounterComponent userCounterComponent;
  @Mock private RabbitMQSettings rabbitMQSettings;
  @Mock private Message message;

  @InjectMocks private UserService userService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(userService, "defaultStatus", "online");
  }

  @Test
  void add() {
    String keyInProgressExpected = "userState::add::IN_PROGRESS";
    AddIn add = new AddIn("Tester", "aaaaaaaa-fdb5-442a-9493-9797c3ab8736");
    AddIn addExpected = new AddIn("Tester", "aaaaaaaa-fdb5-442a-9493-9797c3ab8736");
    String hashKeyExpected = "9e7cd9cb5a63a3591e16f4d835f32a1c4a84ab66e39ae27aa448c03b66bf63e7";

    Users user = new Users();
    user.setId(UUID.fromString("88239958-fdb5-442a-9493-9797c3ab8736"));
    user.setUsername("Tester");
    Users userExpected = new Users();
    userExpected.setId(UUID.fromString("88239958-fdb5-442a-9493-9797c3ab8736"));
    userExpected.setUsername("Tester");
    String queueNameExpected = "user.add.Tester";
    List<Users> usersEmpty = Collections.emptyList();
    List<Users> users = Collections.singletonList(user);
    CallbackMQ<Object> callbackMQExpected =
        new CallbackMQ<>("Tester", "aaaaaaaa-fdb5-442a-9493-9797c3ab8736", add);
    QueueConf queueConf = new QueueConf();
    QueueConf queueConfExpected = new QueueConf();

    when(rabbitMQSettings.getUserAdd()).thenReturn(queueConf);
    when(queueService.getRabbitMQSettings()).thenReturn(rabbitMQSettings);

    when(stateDAO.add(eq(keyInProgressExpected), eq(hashKeyExpected), eq(addExpected)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn("object", (Object) null);
    when(stateDAO.del(eq(keyInProgressExpected), eq(hashKeyExpected)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(true);

    when(usersDAO.findByStatusNameIgnoreCaseAndUsername(eq("online"), eq("Tester")))
        .thenReturn(users)
        .thenReturn(usersEmpty);
    doThrow(new RuntimeException("Test"))
        .when(queueService)
        .sendMessage(eq(queueNameExpected), eq(callbackMQExpected), eq(queueConfExpected));
    assertThat(catchThrowable(() -> userService.add(add)))
        .as("add some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(userService.add(add))
        .as("add user state is in progress")
        .isEqualTo(States.IN_PROGRESS);
    assertThat(catchThrowable(() -> userService.add(add)))
        .as("del if username found exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(catchThrowable(() -> userService.add(add)))
        .as("sendMessage some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    doNothing()
        .when(queueService)
        .sendMessage(eq(queueNameExpected), eq(callbackMQExpected), eq(queueConfExpected));
    assertThat(userService.add(add))
        .as("add user by username is resolve")
        .isEqualTo(States.RESOLVE);
  }

  @Test
  void listenUserAdd() {
    String keyInProgressExpected = "userState::add::IN_PROGRESS";
    String hashKeyExpected = "9e7cd9cb5a63a3591e16f4d835f32a1c4a84ab66e39ae27aa448c03b66bf63e7";
    AddIn add = new AddIn();
    add.setUsername("Tester");
    add.setResId("aaaaaaaa-fdb5-442a-9493-9797c3ab8736");
    Users user = new Users();
    user.setId(UUID.fromString("88239958-fdb5-442a-9493-9797c3ab8736"));
    user.setUsername("Tester");
    Users userNewStatus = new Users();
    userNewStatus.setId(UUID.fromString("bbbbbbbb-fdb5-442a-9493-9797c3ab8736"));
    userNewStatus.setUsername("Tester");
    Users userExpected = new Users();
    userExpected.setId(UUID.fromString("88239958-fdb5-442a-9493-9797c3ab8736"));
    userExpected.setUsername("Tester");
    Users userExpectedNewStatus = new Users();
    userExpectedNewStatus.setId(UUID.fromString("bbbbbbbb-fdb5-442a-9493-9797c3ab8736"));
    userExpectedNewStatus.setUsername("Tester");
    AddOut addOutExpected =
        new AddOut("88239958-fdb5-442a-9493-9797c3ab8736", "aaaaaaaa-fdb5-442a-9493-9797c3ab8736");
    AddOut addOutNewUserStatusExpected =
        new AddOut("bbbbbbbb-fdb5-442a-9493-9797c3ab8736", "aaaaaaaa-fdb5-442a-9493-9797c3ab8736");
    AddIn addExpected = new AddIn();
    add.setUsername("Tester");
    add.setResId("aaaaaaaa-fdb5-442a-9493-9797c3ab8736");
    CallbackMQ<AddIn> in = new CallbackMQ<>("Tester", "aaaaaaaa-fdb5-442a-9493-9797c3ab8736", add);
    CallbackMQ<Object> outExpected =
        new CallbackMQ<>("Tester", "aaaaaaaa-fdb5-442a-9493-9797c3ab8736", addOutExpected);
    CallbackMQ<Object> outExpectedNewStatus =
        new CallbackMQ<>(
            "Tester", "aaaaaaaa-fdb5-442a-9493-9797c3ab8736", addOutNewUserStatusExpected);
    QueueConf queueConf = new QueueConf();
    queueConf.setExchange("testExchange");
    QueueConf queueConfExpected = new QueueConf();
    queueConfExpected.setExchange("testExchange");
    Status status = new Status();
    List<Status> statusesEmpty = Collections.emptyList();
    List<Status> statuses = Collections.singletonList(new Status());
    List<Users> usersEmpty = Collections.emptyList();
    List<Users> users = Collections.singletonList(userNewStatus);

    when(rabbitMQSettings.getWebsocket()).thenReturn(queueConf);
    when(rabbitMQSettings.getUserAdd()).thenReturn(queueConf);
    when(queueService.getRabbitMQSettings()).thenReturn(rabbitMQSettings);
    when(queueService.isGoRetry(eq(message)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(false, false, false, true);
    when(stateDAO.del(eq(keyInProgressExpected), eq(hashKeyExpected)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(true);
    when(statusDAO.findByNameIgnoreCase(eq("online")))
        .thenReturn(statusesEmpty)
        .thenReturn(statuses);
    when(usersDAO.findByUsername(eq("Tester"))).thenReturn(users).thenReturn(usersEmpty);
    when(usersDAO.save(any(Users.class))).thenReturn(userNewStatus).thenReturn(user);
    doThrow(new RuntimeException("Test"))
        .when(queueService)
        .sendMessage(eq("websocket.Tester"), eq(outExpectedNewStatus), eq(queueConfExpected));
    doThrow(new RuntimeException("Test"))
        .when(queueService)
        .removeDeclares(eq("user.add.Tester"), eq("testExchange"));
    assertThat(catchThrowable(() -> userService.listenUserAdd(message, in)))
        .as("isGoRetry some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(catchThrowable(() -> userService.listenUserAdd(message, in)))
        .as("del in progress some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(catchThrowable(() -> userService.listenUserAdd(message, in)))
        .as("removeDeclares some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    doNothing().when(queueService).removeDeclares(eq("user.add.Tester"), eq("testExchange"));
    // Successful delete in progress state and declared queue
    userService.listenUserAdd(message, in);
    // If default status not found delete in progress state and declared queue
    userService.listenUserAdd(message, in);
    assertThat(catchThrowable(() -> userService.listenUserAdd(message, in)))
        .as("sendMessage some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    doNothing()
        .when(queueService)
        .sendMessage(eq("websocket.Tester"), eq(outExpected), eq(queueConfExpected));
    // Successful delete in progress and declared queue and send message to websocket
    userService.listenUserAdd(message, in);
  }

  @Test
  void updateCounterAndStatus() {
    String keyInProgressExpected = "userState::updateCounterAndStatus::IN_PROGRESS";
    UpdateIn updateIn = new UpdateIn();
    updateIn.setUserId("88239958-fdb5-442a-9493-9797c3ab8736");
    updateIn.setStatusId(1);
    updateIn.setResId("aaaaaaaa-fdb5-442a-9493-9797c3ab8736");
    UpdateIn updateInExpected = new UpdateIn();
    updateInExpected.setUserId("88239958-fdb5-442a-9493-9797c3ab8736");
    updateInExpected.setStatusId(1);
    updateInExpected.setResId("aaaaaaaa-fdb5-442a-9493-9797c3ab8736");
    String hashKeyExpected = "3a8b5fd19043a5fc90197f1d423e0a58b8bd98cb6d8859d5edc3c543f634a9ad";
    String userIdHashExpected = "3496fdbcd7ecef849bec992d9441d86fe8cba183882421327c37a9ed45e70b7d";

    Users user = new Users();
    user.setId(UUID.fromString("88239958-fdb5-442a-9493-9797c3ab8736"));
    user.setUsername("Tester");
    user.setStatus(new Status());
    String queueNameExpected = "user.update.Tester";
    CallbackMQ<StatusMQ> callbackMQExpected =
        new CallbackMQ<>(
            "Tester", "aaaaaaaa-fdb5-442a-9493-9797c3ab8736", new StatusMQ(user, updateInExpected));
    QueueConf queueConf = new QueueConf();
    QueueConf queueConfExpected = new QueueConf();

    when(rabbitMQSettings.getUserUpdate()).thenReturn(queueConf);
    when(queueService.getRabbitMQSettings()).thenReturn(rabbitMQSettings);

    when(stateDAO.add(eq(keyInProgressExpected), eq(hashKeyExpected), eq(updateInExpected)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn("object", (Object) null);
    when(userCounterComponent.getUserOrNotFound(
            eq(UUID.fromString("88239958-fdb5-442a-9493-9797c3ab8736")),
            eq("userState::updateCounterAndStatus::%s"),
            eq(hashKeyExpected),
            eq(userIdHashExpected)))
        .thenThrow(new UserIDNotFoundException())
        .thenReturn(user);
    doThrow(new RuntimeException("Test"))
        .when(queueService)
        .sendMessage(eq(queueNameExpected), eq(callbackMQExpected), eq(queueConfExpected));
    assertThat(catchThrowable(() -> userService.updateCounterAndStatus(updateIn)))
        .as("add some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(userService.updateCounterAndStatus(updateIn))
        .as("update user state is in progress")
        .isEqualTo(States.IN_PROGRESS);
    assertThat(catchThrowable(() -> userService.updateCounterAndStatus(updateIn)))
        .as("user ID not found exception")
        .isInstanceOf(UserIDNotFoundException.class);
    assertThat(catchThrowable(() -> userService.updateCounterAndStatus(updateIn)))
        .as("sendMessage some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    doNothing()
        .when(queueService)
        .sendMessage(eq(queueNameExpected), eq(callbackMQExpected), eq(queueConfExpected));
    assertThat(userService.updateCounterAndStatus(updateIn))
        .as("update user state is resolve")
        .isEqualTo(States.RESOLVE);
  }

  @Test
  void listenUserUpdate() {
    String keyInProgressExpected = "userState::updateCounterAndStatus::IN_PROGRESS";
    String keyInitExpected = "userState::updateCounterAndStatus::INIT";
    String hashKeyExpected = "3a8b5fd19043a5fc90197f1d423e0a58b8bd98cb6d8859d5edc3c543f634a9ad";
    UpdateIn updateIn = new UpdateIn();
    updateIn.setUserId("88239958-fdb5-442a-9493-9797c3ab8736");
    updateIn.setStatusId(1);
    updateIn.setResId("aaaaaaaa-fdb5-442a-9493-9797c3ab8736");
    Users user = new Users();
    user.setId(UUID.fromString("88239958-fdb5-442a-9493-9797c3ab8736"));
    user.setUsername("Tester");
    user.setStatus(new Status("Online"));
    UpdateOut updateOutExpected =
        new UpdateOut(
            "88239958-fdb5-442a-9493-9797c3ab8736", "aaaaaaaa-fdb5-442a-9493-9797c3ab8736");
    UpdateIn updateInExpected = new UpdateIn();
    updateInExpected.setUserId("88239958-fdb5-442a-9493-9797c3ab8736");
    updateInExpected.setStatusId(1);
    updateInExpected.setResId("aaaaaaaa-fdb5-442a-9493-9797c3ab8736");
    Users userExpected = new Users();
    userExpected.setId(UUID.fromString("88239958-fdb5-442a-9493-9797c3ab8736"));
    userExpected.setUsername("Tester");
    userExpected.setStatus(new Status("Online"));
    CallbackMQ<StatusMQ> in =
        new CallbackMQ<>(
            "Tester", "aaaaaaaa-fdb5-442a-9493-9797c3ab8736", new StatusMQ(user, updateIn));
    CallbackMQ<Object> outExpected =
        new CallbackMQ<>("Tester", "aaaaaaaa-fdb5-442a-9493-9797c3ab8736", updateOutExpected);
    QueueConf queueConf = new QueueConf();
    queueConf.setExchange("testExchange");
    QueueConf queueConfExpected = new QueueConf();
    queueConfExpected.setExchange("testExchange");
    Types type = new Types();
    List<Types> typesEmpty = Collections.emptyList();
    List<Types> types = Collections.singletonList(type);

    when(rabbitMQSettings.getWebsocket()).thenReturn(queueConf);
    when(rabbitMQSettings.getUserUpdate()).thenReturn(queueConf);
    when(queueService.getRabbitMQSettings()).thenReturn(rabbitMQSettings);
    when(queueService.isGoRetry(eq(message)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(false, false, false, true);
    when(stateDAO.del(eq(keyInProgressExpected), eq(hashKeyExpected)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(true);
    when(typesDAO.findByNameIgnoreCase(eq("Online"))).thenReturn(typesEmpty).thenReturn(types);
    when(usersDAO.save(eq(userExpected))).thenReturn(user);
    doThrow(new RuntimeException("Test"))
        .when(userCounterComponent)
        .incCounter(
            any(),
            eq(userExpected),
            isNull(),
            eq("userState::updateCounterAndStatus::%s"),
            eq(hashKeyExpected));
    doThrow(new RuntimeException("Test"))
        .when(queueService)
        .sendMessage(eq("websocket.Tester"), eq(outExpected), eq(queueConfExpected));
    when(stateDAO.del(eq(keyInitExpected), eq(hashKeyExpected)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(true, true);
    doThrow(new RuntimeException("Test"))
        .when(queueService)
        .removeDeclares(eq("user.update.Tester"), eq("testExchange"));
    assertThat(catchThrowable(() -> userService.listenUserUpdate(message, in)))
        .as("isGoRetry some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(catchThrowable(() -> userService.listenUserUpdate(message, in)))
        .as("del in progress some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(catchThrowable(() -> userService.listenUserUpdate(message, in)))
        .as("removeDeclares some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    doNothing().when(queueService).removeDeclares(eq("user.update.Tester"), eq("testExchange"));
    // Successful delete in progress state and declared queue
    userService.listenUserUpdate(message, in);
    assertThat(catchThrowable(() -> userService.listenUserUpdate(message, in)))
        .as("incCounter some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    doNothing()
        .when(userCounterComponent)
        .incCounter(
            any(),
            eq(userExpected),
            isNull(),
            eq("userState::updateCounterAndStatus::%s"),
            eq(hashKeyExpected));
    assertThat(catchThrowable(() -> userService.listenUserUpdate(message, in)))
        .as("sendMessage some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    doNothing()
        .when(queueService)
        .sendMessage(eq("websocket.Tester"), eq(outExpected), eq(queueConfExpected));
    assertThat(catchThrowable(() -> userService.listenUserUpdate(message, in)))
        .as("del init some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    // Successful delete in progress and init state, declared queue and send message to websocket
    userService.listenUserUpdate(message, in);
  }

  @Test
  void all() {
    AllIn allIn = new AllIn("testUsername", "testUserId", "testResId", "testPath");
    AllIn allInExpected = new AllIn("testUsername", "testUserId", "testResId", "testPath");
    List<Users> typesEmpty = Collections.emptyList();
    List<Users> typesEmptyExpected = Collections.emptyList();
    List<Users> types = Collections.singletonList(new Users());
    List<Users> typesExpected = Collections.singletonList(new Users());
    when(usersDAO.findAll(Mockito.<Specification<Users>>any()))
        .thenReturn(typesEmpty)
        .thenReturn(types);
    AllOut<Users> allOut = userService.all(allIn);
    assertThat(allOut.getEtag()).as("Etag not null").isNotNull().isNotEmpty();
    assertThat(allOut.getData()).as("User list is empty").isNotNull().isEqualTo(typesEmptyExpected);
    allOut = userService.all(allIn);
    assertThat(allOut.getEtag()).as("Etag not null").isNotNull().isNotEmpty();
    assertThat(allOut.getData()).as("User list is not empty").isNotNull().isEqualTo(typesExpected);
  }
}
