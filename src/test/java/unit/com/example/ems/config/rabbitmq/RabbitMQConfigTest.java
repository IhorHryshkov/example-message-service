package unit.com.example.ems.config.rabbitmq;

import com.example.ems.config.rabbitmq.RabbitMQConfig;
import com.example.ems.config.rabbitmq.RabbitMQSettings;
import com.example.ems.dto.mq.QueueConf;
import com.greghaskins.spectrum.Spectrum;
import org.junit.Rule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(Spectrum.class)
class RabbitMQConfigTest {
	@Mock
	private RabbitMQSettings  rabbitMQSettings;
	@Mock
	private QueueConf         userAdd;
	@Mock
	private QueueConf         userUpdate;
	@Mock
	private QueueConf         counterAdd;
	@Mock
	private QueueConf         websocket;
	@Mock
	private ConnectionFactory connectionFactory;
	@Mock
	private Connection        connection;
	@Mock
	private RabbitTemplate    rabbitTemplate;
	@InjectMocks
	private RabbitMQConfig    rabbitMQConfig;

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	{
		describe("RabbitMQConfig object test", () -> {
			describe("'jsonMessageConverter' method test", () -> {
				it(
						"Should return is not null value",
						() -> assertThat(rabbitMQConfig.jsonMessageConverter()).isNotNull()
				);
				it(
						"Should return is Jackson2JsonMessageConverter object",
						() -> assertThat(rabbitMQConfig.jsonMessageConverter()).isOfAnyClassIn(
								Jackson2JsonMessageConverter.class)
				);
			});
			describe("'amqpTemplate' method test", () -> {
				beforeEach(() -> {
					when(connectionFactory.createConnection()).thenReturn(connection);
				});
				it(
						"Should return is not null value",
						() -> assertThat(rabbitMQConfig.amqpTemplate(connectionFactory)).isNotNull()
				);
				it(
						"Should return is RabbitTemplate object",
						() -> assertThat(rabbitMQConfig.amqpTemplate(connectionFactory)).isOfAnyClassIn(RabbitTemplate.class)
				);
				it(
						"Should return is Jackson2JsonMessageConverter object for message converter",
						() -> assertThat(((RabbitTemplate) rabbitMQConfig.amqpTemplate(connectionFactory)).getMessageConverter())
								.isOfAnyClassIn(Jackson2JsonMessageConverter.class)
				);
			});
			describe("'defaultRabbitListenerEndpointRegistry' method test", () -> {
				it(
						"Should return is not null value",
						() -> assertThat(rabbitMQConfig.defaultRabbitListenerEndpointRegistry()).isNotNull()
				);
				it(
						"Should return is RabbitListenerEndpointRegistry object",
						() -> assertThat(rabbitMQConfig.defaultRabbitListenerEndpointRegistry()).isOfAnyClassIn(
								RabbitListenerEndpointRegistry.class)
				);
			});
		});
	}
}
