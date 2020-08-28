package unit.com.example.ems.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.example.ems.config.rabbitmq.RabbitMQSettings;
import com.example.ems.dto.mq.QueueConf;
import com.example.ems.services.QueueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {
  @Mock private AmqpAdmin amqpAdmin;
  @Mock private AmqpTemplate amqpTemplate;
  @Mock private RabbitMQSettings rabbitMQSettings;
  @Mock private RabbitListenerEndpointRegistry listenerMQRegistry;
  @Mock private SimpleMessageListenerContainer listener;

  @InjectMocks private QueueService queueService;

  @Test
  void sendMessage() {
    String queueName = "Tester";
    String queueNameExpected = "Tester";
    String data = "testData";
    String dataExpected = "testData";
    QueueConf queueConf = new QueueConf("testExchange", "testRouting", true, true, true);

    doThrow(new RuntimeException("Test")).when(listener).addQueueNames(eq(queueNameExpected));
    when(listenerMQRegistry.getListenerContainer(eq("testExchange"))).thenReturn(listener);
    when(amqpAdmin.declareQueue(any(Queue.class)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn("queueName");
    doThrow(new RuntimeException("Test")).when(amqpAdmin).declareBinding(any(Binding.class));
    doThrow(new AmqpException("Test"))
        .when(amqpTemplate)
        .convertAndSend(eq("testExchange"), eq(queueNameExpected), eq(dataExpected));
    assertThat(catchThrowable(() -> queueService.sendMessage(queueName, data, queueConf)))
        .as("declareQueue some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(catchThrowable(() -> queueService.sendMessage(queueName, data, queueConf)))
        .as("declareBinding some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    doNothing().when(amqpAdmin).declareBinding(any(Binding.class));
    assertThat(catchThrowable(() -> queueService.sendMessage(queueName, data, queueConf)))
        .as("convertAndSend some exception")
        .isInstanceOf(AmqpException.class);
    doNothing()
        .when(amqpTemplate)
        .convertAndSend(eq("testExchange"), eq(queueNameExpected), eq(dataExpected));
    assertThat(catchThrowable(() -> queueService.sendMessage(queueName, data, queueConf)))
        .as("addQueueNames some exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    doNothing().when(listener).addQueueNames(eq(queueNameExpected));
    // Successful declare queue, binding, message listener and send message
    queueService.sendMessage(queueName, data, queueConf);
  }

  @Test
  void isGoRetry() {}

  @Test
  void removeDeclares() {}

  @Test
  void initQueueListener() {}

  @Test
  void removeQueueListener() {}

  @Test
  void initQueuesListeners() {}
}
