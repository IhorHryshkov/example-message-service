package unit.com.example.ems.config.rabbitmq;

import com.example.ems.config.rabbitmq.RabbitMQConfig;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(Spectrum.class)
class RabbitMQConfigTest {
	@Mock
	private ConnectionFactory connectionFactory;
	@Mock
	private Connection        connection;
	@InjectMocks
	private RabbitMQConfig    rabbitMQConfig;

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	{
		describe("RabbitMQConfig 'jsonMessageConverter' method test", () -> {
			it(
					"Should return is not null value",
					() -> assertThat(rabbitMQConfig.jsonMessageConverter())
							.as("Jackson2JsonMessageConverter is not null")
							.isNotNull()
			);
			it(
					"Should return is Jackson2JsonMessageConverter class",
					() -> assertThat(rabbitMQConfig.jsonMessageConverter())
							.as("Jackson2JsonMessageConverter classname")
							.isOfAnyClassIn(Jackson2JsonMessageConverter.class)
			);
		});
		describe("RabbitMQConfig 'amqpTemplate' method test", () -> {
			beforeEach(() -> {
				when(connectionFactory.createConnection()).thenReturn(connection);
			});
			it(
					"Should return is not null value",
					() -> assertThat(rabbitMQConfig.amqpTemplate(connectionFactory))
							.as("RabbitTemplate is not null")
							.isNotNull()
			);
			it(
					"Should return is RabbitTemplate class",
					() -> assertThat(rabbitMQConfig.amqpTemplate(connectionFactory))
							.as("RabbitTemplate classname")
							.isOfAnyClassIn(RabbitTemplate.class)
			);
			it(
					"Should return is Jackson2JsonMessageConverter class for message converter",
					() -> assertThat(((RabbitTemplate) rabbitMQConfig.amqpTemplate(connectionFactory)).getMessageConverter())
							.as("MessageConverter classname")
							.isOfAnyClassIn(Jackson2JsonMessageConverter.class)
			);
		});
	}
}
