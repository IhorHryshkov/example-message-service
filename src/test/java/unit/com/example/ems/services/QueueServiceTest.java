package unit.com.example.ems.services;

import com.example.ems.config.rabbitmq.RabbitMQSettings;
import com.example.ems.services.QueueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {
  @Mock private AmqpAdmin amqpAdmin;
  @Mock private AmqpTemplate amqpTemplate;
  @Mock private RabbitMQSettings rabbitMQSettings;
  @Mock private RabbitListenerEndpointRegistry listenerMQRegistry;

  @InjectMocks private QueueService queueService;

  @Test
  void sendMessage() {}

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
