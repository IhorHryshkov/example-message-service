/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-06T21:00
 */
package com.example.ems.services;

import com.example.ems.config.rabbitmq.RabbitMQSettings;
import com.example.ems.dto.mq.QueueBind;
import com.example.ems.dto.mq.QueueConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class QueueService {
	private final AmqpAdmin amqpAdmin;
	private final AmqpTemplate amqpTemplate;
	private final RabbitMQSettings rabbitMQSettings;
	private final RabbitListenerEndpointRegistry listenerMQRegistry;

	QueueService(
			AmqpAdmin amqpAdmin,
			AmqpTemplate amqpTemplate,
			RabbitMQSettings rabbitMQSettings,
			RabbitListenerEndpointRegistry listenerMQRegistry
	) {
		this.amqpAdmin = amqpAdmin;
		this.amqpTemplate = amqpTemplate;
		this.listenerMQRegistry = listenerMQRegistry;
		this.rabbitMQSettings = rabbitMQSettings;
	}

	public RabbitMQSettings getRabbitMQSettings() {
		return this.rabbitMQSettings;
	}

	public void sendMessage(String queueName, Object data, QueueConf conf) {
		QueueBind queueBind = buildQueueAndBind(queueName, conf);
		amqpAdmin.declareQueue(queueBind.getQueue());
		amqpAdmin.declareBinding(queueBind.getBinding());
		amqpTemplate.convertAndSend(conf.getExchange(), queueName, data);
		initQueueListener(queueName, conf.getExchange());
		log.debug("getQueueProperties2: {}", amqpAdmin.getQueueProperties(queueName));
	}

	public boolean isGoRetry(Message message) {
		List<Map<String, ?>> xDeathHeader = message.getMessageProperties().getXDeathHeader();
		if (xDeathHeader != null && !xDeathHeader.isEmpty()) {
			Long count = (Long) xDeathHeader.stream().findFirst().filter(x -> x.containsKey("count")).map(x -> x.get("count")).orElse(null);
			return count == null || count < this.rabbitMQSettings.getRetryCount();
		}
		return true;
	}

	public void removeDeclares(String queueName, String id) {
		removeQueueListener(queueName, id);
	}

	private QueueBind buildQueueAndBind(String queueName, QueueConf conf) {
		QueueBuilder queueBuilder = conf.getDurable() ? QueueBuilder.durable(queueName) : QueueBuilder.nonDurable(queueName);
		if (conf.getExclusive()) {
			queueBuilder.exclusive();
		}
		if (conf.getAutoDelete()) {
			queueBuilder.autoDelete();
		}
		;
		Queue queue = queueBuilder.deadLetterExchange(conf.getExchange()).deadLetterRoutingKey(queueName).build();
		log.debug("getQueueProperties1: {}", amqpAdmin.getQueueProperties(queueName));
		DirectExchange exchange = ExchangeBuilder.directExchange(conf.getExchange()).build();
		Binding binding = BindingBuilder.bind(queue).to(exchange).with(queueName);
		return new QueueBind(queue, binding);
	}

	public void initQueueListener(String queueName, String id) {
		SimpleMessageListenerContainer listener = (SimpleMessageListenerContainer) listenerMQRegistry.getListenerContainer(id);
		listener.addQueueNames(queueName);
	}

	public void removeQueueListener(String queueName, String id) {
		SimpleMessageListenerContainer listener = (SimpleMessageListenerContainer) listenerMQRegistry.getListenerContainer(id);
		listener.removeQueueNames(queueName);
	}

	public void initQueuesListeners(String[] queuesNames, String id) {
		for (String queueName : queuesNames) {
			initQueueListener(queueName, id);
		}
	}
}
