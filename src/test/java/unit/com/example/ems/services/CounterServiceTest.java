package unit.com.example.ems.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.example.ems.config.rabbitmq.RabbitMQSettings;
import com.example.ems.database.dao.pg.CountersDAO;
import com.example.ems.database.dao.pg.TypesDAO;
import com.example.ems.database.dao.redis.StateDAO;
import com.example.ems.dto.database.pg.Counters;
import com.example.ems.dto.database.pg.Types;
import com.example.ems.dto.database.pg.Users;
import com.example.ems.dto.mq.CallbackMQ;
import com.example.ems.dto.mq.CounterMQ;
import com.example.ems.dto.mq.QueueConf;
import com.example.ems.dto.network.controller.counter.AddIn;
import com.example.ems.dto.network.controller.counter.AddOut;
import com.example.ems.dto.network.controller.counter.GetByIdIn;
import com.example.ems.dto.network.controller.counter.GetByIdOut;
import com.example.ems.network.controllers.exceptions.status.UserIDNotFoundException;
import com.example.ems.services.CounterService;
import com.example.ems.services.QueueService;
import com.example.ems.services.components.UserCounterComponent;
import com.example.ems.utils.enums.States;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;

@ExtendWith(MockitoExtension.class)
class CounterServiceTest {
  @Mock private CountersDAO countersDAO;
  @Mock private StateDAO stateDAO;
  @Mock private TypesDAO typeDAO;
  @Mock private QueueService queueService;
  @Mock private UserCounterComponent userCounterComponent;
  @Mock private RabbitMQSettings rabbitMQSettings;
  @Mock private Message message;

  @InjectMocks private CounterService counterService;

  @Test
  void getByUserId() {
    UUID userIdExpected = UUID.fromString("aaaaaaaa-86f0-4b01-a18e-def71dbda1ba");
    GetByIdIn in =
        new GetByIdIn(
            userIdExpected.toString(), "bbbbbbbb-86f0-4b01-a18e-def71dbda1ba", "/v1/test");
    List<Counters> countersExpected = Collections.singletonList(new Counters());
    when(countersDAO.findByKeysUserId(eq(userIdExpected)))
        .thenReturn(Collections.emptyList())
        .thenReturn(Collections.singletonList(new Counters()));
    // If return empty
    GetByIdOut<List<Counters>> out = counterService.getByUserId(in);
    assertThat(out.getEtag()).as("ETag").isNotNull();
    assertThat(out.getData()).as("List of counters").isEqualTo(Collections.emptyList());
    // If return not empty
    out = counterService.getByUserId(in);
    assertThat(out.getEtag()).as("ETag").isNotNull();
    assertThat(out.getData()).as("List of counters").isEqualTo(countersExpected);
  }

  @Test
  void add() {
    String keyInProgressExpected = "counterState::add::IN_PROGRESS";
    AddIn add = new AddIn();
    add.setUserId("88239958-fdb5-442a-9493-9797c3ab8736");
    add.setTypeId(1);
    add.setCount(1L);
    add.setResId("aaaaaaaa-fdb5-442a-9493-9797c3ab8736");
    AddIn addExpected = new AddIn();
    addExpected.setUserId("88239958-fdb5-442a-9493-9797c3ab8736");
    addExpected.setTypeId(1);
    addExpected.setCount(1L);
    addExpected.setResId("aaaaaaaa-fdb5-442a-9493-9797c3ab8736");
    String hashKeyExpected = "8a69a6ef51814f854047e49ed1a539d33f6096ec858454ed3499c9a918fcd8d7";
    String userIdHashExpected = "3496fdbcd7ecef849bec992d9441d86fe8cba183882421327c37a9ed45e70b7d";

    Users user = new Users();
    user.setId(UUID.fromString("88239958-fdb5-442a-9493-9797c3ab8736"));
    user.setUsername("Tester");
    String queueNameExpected = "counter.add.Tester";
    CallbackMQ<CounterMQ> callbackMQExpected =
        new CallbackMQ<>(
            "Tester", "aaaaaaaa-fdb5-442a-9493-9797c3ab8736", new CounterMQ(user, addExpected));
    QueueConf queueConf = new QueueConf();
    QueueConf queueConfExpected = new QueueConf();

    when(rabbitMQSettings.getCounterAdd()).thenReturn(queueConf);
    when(queueService.getRabbitMQSettings()).thenReturn(rabbitMQSettings);

    when(stateDAO.add(eq(keyInProgressExpected), eq(hashKeyExpected), eq(addExpected)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn("object", (Object) null);
    when(userCounterComponent.getUserOrNotFound(
            eq(UUID.fromString("88239958-fdb5-442a-9493-9797c3ab8736")),
            eq("counterState::add::%s"),
            eq(hashKeyExpected),
            eq(userIdHashExpected)))
        .thenThrow(new UserIDNotFoundException())
        .thenReturn(user);
    doThrow(new RuntimeException("Test"))
        .when(queueService)
        .sendMessage(eq(queueNameExpected), eq(callbackMQExpected), eq(queueConfExpected));
    assertThat(catchThrowable(() -> counterService.add(add)))
        .as("add some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(counterService.add(add))
        .as("add user state is in progress")
        .isEqualTo(States.IN_PROGRESS);
    assertThat(catchThrowable(() -> counterService.add(add)))
        .as("user ID not found exception")
        .isInstanceOf(UserIDNotFoundException.class);
    assertThat(catchThrowable(() -> counterService.add(add)))
        .as("sendMessage some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    doNothing()
        .when(queueService)
        .sendMessage(eq(queueNameExpected), eq(callbackMQExpected), eq(queueConfExpected));
    assertThat(counterService.add(add)).as("add user state is resolve").isEqualTo(States.RESOLVE);
  }

  @Test
  void listenCounterAdd() {
    String keyInProgressExpected = "counterState::add::IN_PROGRESS";
    String keyInitExpected = "counterState::add::INIT";
    String hashKeyExpected = "8a69a6ef51814f854047e49ed1a539d33f6096ec858454ed3499c9a918fcd8d7";
    AddIn add = new AddIn();
    add.setUserId("88239958-fdb5-442a-9493-9797c3ab8736");
    add.setTypeId(1);
    add.setCount(1L);
    add.setResId("aaaaaaaa-fdb5-442a-9493-9797c3ab8736");
    Users user = new Users();
    user.setId(UUID.fromString("88239958-fdb5-442a-9493-9797c3ab8736"));
    user.setUsername("Tester");
    AddOut addOutExpected =
        new AddOut("88239958-fdb5-442a-9493-9797c3ab8736", "aaaaaaaa-fdb5-442a-9493-9797c3ab8736");
    AddIn addExpected = new AddIn();
    addExpected.setUserId("88239958-fdb5-442a-9493-9797c3ab8736");
    addExpected.setTypeId(1);
    addExpected.setCount(1L);
    addExpected.setResId("aaaaaaaa-fdb5-442a-9493-9797c3ab8736");
    Users userExpected = new Users();
    userExpected.setId(UUID.fromString("88239958-fdb5-442a-9493-9797c3ab8736"));
    userExpected.setUsername("Tester");
    CallbackMQ<CounterMQ> in =
        new CallbackMQ<>(
            "Tester", "aaaaaaaa-fdb5-442a-9493-9797c3ab8736", new CounterMQ(user, add));
    CallbackMQ<Object> outExpected =
        new CallbackMQ<>("Tester", "aaaaaaaa-fdb5-442a-9493-9797c3ab8736", addOutExpected);
    QueueConf queueConf = new QueueConf();
    queueConf.setExchange("testExchange");
    QueueConf queueConfExpected = new QueueConf();
    queueConfExpected.setExchange("testExchange");
    Types types = new Types();

    when(rabbitMQSettings.getWebsocket()).thenReturn(queueConf);
    when(rabbitMQSettings.getCounterAdd()).thenReturn(queueConf);
    when(queueService.getRabbitMQSettings()).thenReturn(rabbitMQSettings);
    when(queueService.isGoRetry(eq(message)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(false, false, false, true);
    when(stateDAO.del(eq(keyInProgressExpected), eq(hashKeyExpected)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(true);
    when(typeDAO.findById(eq(1)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(types));
    doThrow(new RuntimeException("Test"))
        .when(userCounterComponent)
        .incCounter(
            any(), eq(userExpected), eq(1L), eq("counterState::add::%s"), eq(hashKeyExpected));
    doThrow(new RuntimeException("Test"))
        .when(queueService)
        .sendMessage(eq("websocket.Tester"), eq(outExpected), eq(queueConfExpected));
    when(stateDAO.del(eq(keyInitExpected), eq(hashKeyExpected)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(true, true);
    doThrow(new RuntimeException("Test"))
        .when(queueService)
        .removeDeclares(eq("counter.add.Tester"), eq("testExchange"));
    assertThat(catchThrowable(() -> counterService.listenCounterAdd(message, in)))
        .as("isGoRetry some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(catchThrowable(() -> counterService.listenCounterAdd(message, in)))
        .as("del in progress some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(catchThrowable(() -> counterService.listenCounterAdd(message, in)))
        .as("removeDeclares some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    doNothing().when(queueService).removeDeclares(eq("counter.add.Tester"), eq("testExchange"));
    // Successful delete in progress state and declared queue
    counterService.listenCounterAdd(message, in);
    assertThat(catchThrowable(() -> counterService.listenCounterAdd(message, in)))
        .as("findById some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(catchThrowable(() -> counterService.listenCounterAdd(message, in)))
        .as("incCounter some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    doNothing()
        .when(userCounterComponent)
        .incCounter(
            any(), eq(userExpected), eq(1L), eq("counterState::add::%s"), eq(hashKeyExpected));
    assertThat(catchThrowable(() -> counterService.listenCounterAdd(message, in)))
        .as("sendMessage some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    doNothing()
        .when(queueService)
        .sendMessage(eq("websocket.Tester"), eq(outExpected), eq(queueConfExpected));
    assertThat(catchThrowable(() -> counterService.listenCounterAdd(message, in)))
        .as("del init some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    // Successful delete in progress and init state, declared queue and send message to websocket
    counterService.listenCounterAdd(message, in);
  }
}
