/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-06T21:00
 */
package com.example.ems.services;

import com.example.ems.config.rabbitmq.RabbitMQSettings;
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

	private Queue queue;
	private DirectExchange exchange;
	private Binding binding;

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
		QueueBuilder queueBuilder = conf.getDurable() ? QueueBuilder.durable(queueName) : QueueBuilder.nonDurable(queueName);
		if (conf.getExclusive()) {
			queueBuilder.exclusive();
		}
		if (conf.getAutoDelete()) {
			queueBuilder.autoDelete();
		}
		queue = queueBuilder.deadLetterExchange(conf.getExchange()).deadLetterRoutingKey(queueName).build();
		log.debug("getQueueProperties1: {}", amqpAdmin.getQueueProperties(queueName));
		exchange = ExchangeBuilder.directExchange(conf.getExchange()).build();
		binding = BindingBuilder.bind(queue).to(exchange).with(queueName);
		amqpAdmin.declareQueue(queue);
		amqpAdmin.declareBinding(binding);
		amqpTemplate.convertAndSend(conf.getExchange(), queueName, data);
		initQueueListener(queueName, conf.getExchange());
		log.debug("getQueueProperties2: {}", amqpAdmin.getQueueProperties(queueName));
	}

	public boolean endedRetryCount(Message message) {
		List<Map<String, ?>> xDeathHeader = message.getMessageProperties().getXDeathHeader();
		if (xDeathHeader != null && !xDeathHeader.isEmpty()) {
			Long count = (Long) xDeathHeader.get(0).get("count");
			return count >= this.rabbitMQSettings.getRetryCount();
		}

		return false;
	}

	public void removeDeclares(String queueName, String id) {
		amqpAdmin.removeBinding(binding);
		removeQueueListener(queueName, id);
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
