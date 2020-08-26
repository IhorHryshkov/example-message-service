/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-26T10:19
 */
package unit.com.example.ems.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.example.ems.config.rabbitmq.RabbitMQSettings;
import com.example.ems.database.dao.redis.StateDAO;
import com.example.ems.dto.mq.CallbackMQ;
import com.example.ems.dto.mq.QueueConf;
import com.example.ems.network.controllers.exceptions.websocket.NoAckException;
import com.example.ems.services.CallbackService;
import com.example.ems.services.QueueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class CallbackServiceTest {
  @Mock private SimpMessagingTemplate simpMessagingTemplate;
  @Mock private StateDAO stateDAO;
  @Mock private QueueService queueService;

  @Mock private RabbitMQSettings rabbitMQSettings;
  @Mock private Message message;

  @InjectMocks private CallbackService callbackService;

  @Test
  void listen() {
    String resolveKeyExpected = "state::callback::RESOLVE";
    String inProgressKeyExpected = "state::callback::IN_PROGRESS";
    String queueName = "testQueue";
    String resId = "testResId";
    String data = "testData";
    String queueNameExpected = "testQueue";
    String resIdExpected = "testResId";
    String dataExpected = "testData";
    String destinationExpected = "/queue/testQueue";
    String noAckExpected = "Waiting RESOLVE by res ID: testResId";
    CallbackMQ<Object> in = new CallbackMQ<>(queueName, resId, data);
    CallbackMQ<Object> inExpected =
        new CallbackMQ<>(queueNameExpected, resIdExpected, dataExpected);
    QueueConf queueConf = new QueueConf();
    QueueConf queueConfExpected = new QueueConf();

    when(rabbitMQSettings.getWebsocket()).thenReturn(queueConf);
    when(queueService.getRabbitMQSettings()).thenReturn(rabbitMQSettings);
    when(queueService.isGoRetry(eq(message)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(false, false, false, true);
    when(stateDAO.exist(eq(resolveKeyExpected), eq(resIdExpected)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(true, true, false);
    when(stateDAO.del(eq(resolveKeyExpected), eq(resIdExpected)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(true);
    when(stateDAO.del(eq(inProgressKeyExpected), eq(resIdExpected)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(true);
    when(stateDAO.add(eq(inProgressKeyExpected), eq(resIdExpected), eq(inExpected)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(null, null, "testData");
    doThrow(new RuntimeException("Test"))
        .when(queueService)
        .sendMessage(eq("websocket.testQueue"), eq(inExpected), eq(queueConfExpected));
    doThrow(new RuntimeException("Test"))
        .when(simpMessagingTemplate)
        .convertAndSend(eq(destinationExpected), eq(dataExpected));

    assertThat(catchThrowable(() -> callbackService.listen(message, in)))
        .as("isGoRetry some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(catchThrowable(() -> callbackService.listen(message, in)))
        .as("sendMessage some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    doNothing()
        .when(queueService)
        .sendMessage(eq("websocket.testQueue"), eq(inExpected), eq(queueConfExpected));
    assertThat(catchThrowable(() -> callbackService.listen(message, in)))
        .as("del in progress some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    // Successful resend exception message to current queue
    callbackService.listen(message, in);
    assertThat(catchThrowable(() -> callbackService.listen(message, in)))
        .as("exist some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(catchThrowable(() -> callbackService.listen(message, in)))
        .as("del resolve some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    // Successful message resolve from client
    callbackService.listen(message, in);
    assertThat(catchThrowable(() -> callbackService.listen(message, in)))
        .as("add some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(catchThrowable(() -> callbackService.listen(message, in)))
        .as("convertAndSend some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    doNothing()
        .when(simpMessagingTemplate)
        .convertAndSend(eq(destinationExpected), eq(dataExpected));
    assertThat(catchThrowable(() -> callbackService.listen(message, in)))
        .as("convertAndSend successful")
        .isInstanceOf(NoAckException.class)
        .hasMessageContaining(noAckExpected);
    assertThat(catchThrowable(() -> callbackService.listen(message, in)))
        .as("Message send in progress")
        .isInstanceOf(NoAckException.class)
        .hasMessageContaining(noAckExpected);
  }

  @Test
  void removeState() {
    String resId = "testResId";
    String resIdExpected = "testResId";
    String keyInProgressExpected = "state::callback::IN_PROGRESS";
    String keyResolveExpected = "state::callback::RESOLVE";

    when(stateDAO.exist(eq(keyInProgressExpected), eq(resIdExpected)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(false, true);
    when(stateDAO.add(eq(keyResolveExpected), eq(resIdExpected), anyLong()))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn("object", "object");
    when(stateDAO.del(eq(keyInProgressExpected), eq(resIdExpected)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(true);
    assertThat(catchThrowable(() -> callbackService.removeState(resId)))
        .as("exist some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    // If resId not found do nothing
    callbackService.removeState(resId);
    assertThat(catchThrowable(() -> callbackService.removeState(resId)))
        .as("add some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(catchThrowable(() -> callbackService.removeState(resId)))
        .as("del some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    // If resId resolve successful add and successful delete in progress state
    callbackService.removeState(resId);
  }
}
