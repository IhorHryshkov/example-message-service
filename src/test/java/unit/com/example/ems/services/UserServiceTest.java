package unit.com.example.ems.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.example.ems.config.rabbitmq.RabbitMQSettings;
import com.example.ems.database.dao.pg.StatusDAO;
import com.example.ems.database.dao.pg.TypesDAO;
import com.example.ems.database.dao.pg.UsersDAO;
import com.example.ems.database.dao.redis.StateDAO;
import com.example.ems.dto.database.pg.Users;
import com.example.ems.dto.mq.CallbackMQ;
import com.example.ems.dto.mq.QueueConf;
import com.example.ems.dto.network.controller.user.AddIn;
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
import org.mockito.junit.jupiter.MockitoExtension;
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
  void listenUserAdd() {}

  @Test
  void updateCounterAndStatus() {}

  @Test
  void listenUserUpdate() {}

  @Test
  void all() {}
}
