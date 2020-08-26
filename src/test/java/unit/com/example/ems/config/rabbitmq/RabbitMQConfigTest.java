/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-13T09:35
 */
package unit.com.example.ems.config.rabbitmq;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ems.config.rabbitmq.RabbitMQConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

@ExtendWith(MockitoExtension.class)
class RabbitMQConfigTest {
  @Mock private ConnectionFactory connectionFactory;
  @Mock private Connection connection;
  @InjectMocks private RabbitMQConfig rabbitMQConfig;

  @Test
  void jsonMessageConverter() {
    assertThat(rabbitMQConfig.jsonMessageConverter())
        .as("Jackson2JsonMessageConverter is not null")
        .isNotNull();
    assertThat(rabbitMQConfig.jsonMessageConverter())
        .as("Class is not Jackson2JsonMessageConverter")
        .isOfAnyClassIn(Jackson2JsonMessageConverter.class);
  }

  @Test
  void amqpTemplate() {
    assertThat(rabbitMQConfig.amqpTemplate(connectionFactory))
        .as("RabbitTemplate is not null")
        .isNotNull();
    assertThat(rabbitMQConfig.amqpTemplate(connectionFactory))
        .as("Class is not RabbitTemplate")
        .isOfAnyClassIn(RabbitTemplate.class);
    assertThat(
            ((RabbitTemplate) rabbitMQConfig.amqpTemplate(connectionFactory)).getMessageConverter())
        .as("Class is not MessageConverter")
        .isOfAnyClassIn(Jackson2JsonMessageConverter.class);
  }
}
