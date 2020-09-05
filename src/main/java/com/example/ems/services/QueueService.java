/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-06T21:00
 */
package com.example.ems.services;

import com.example.ems.config.rabbitmq.RabbitMQSettings;
import com.example.ems.dto.mq.QueueConf;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Service;

/** Processing service of AMQP queues */
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
      RabbitListenerEndpointRegistry listenerMQRegistry) {
    this.amqpAdmin = amqpAdmin;
    this.amqpTemplate = amqpTemplate;
    this.listenerMQRegistry = listenerMQRegistry;
    this.rabbitMQSettings = rabbitMQSettings;
  }

  /**
   * Get rabbit MQ settings
   *
   * @return result object {@link RabbitMQSettings} with settings
   */
  public RabbitMQSettings getRabbitMQSettings() {
    return this.rabbitMQSettings;
  }

  /**
   * Add new queue if not exist. Add new exchange if not exist. Bind queue to exchange and send
   * message to queue.
   *
   * @param queueName Queue name
   * @param data Data for send to queue
   * @param conf Configuration data for new queue {@link QueueConf}
   */
  public void sendMessage(String queueName, Object data, QueueConf conf) {
    QueueBuilder queueBuilder =
        conf.getDurable() ? QueueBuilder.durable(queueName) : QueueBuilder.nonDurable(queueName);
    if (conf.getExclusive()) {
      queueBuilder.exclusive();
    }
    if (conf.getAutoDelete()) {
      queueBuilder.autoDelete();
    }
    Queue queue =
        queueBuilder.deadLetterExchange(conf.getExchange()).deadLetterRoutingKey(queueName).build();
    DirectExchange exchange = ExchangeBuilder.directExchange(conf.getExchange()).build();
    Binding binding = BindingBuilder.bind(queue).to(exchange).with(queueName);
    amqpAdmin.declareQueue(queue);
    amqpAdmin.declareBinding(binding);
    amqpTemplate.convertAndSend(conf.getExchange(), queueName, data);
    initQueueListener(queueName, conf.getExchange());
  }

  /**
   * Check counts retry to send and return true if retry count is less than count AMQP sending error
   * or error header not found. Return false if retry count from setting greater than or equals
   * count AMQP sending error.
   *
   * @param message AMQP message {@link Message} with data
   * @return result is check counts of error
   */
  public boolean isGoRetry(Message message) {
    List<Map<String, ?>> xDeathHeader = message.getMessageProperties().getXDeathHeader();
    if (xDeathHeader != null && !xDeathHeader.isEmpty()) {
      Long count =
          (Long)
              xDeathHeader.stream()
                  .findFirst()
                  .filter(x -> x.containsKey("count"))
                  .map(x -> x.get("count"))
                  .orElse(null);
      return count == null || count < this.rabbitMQSettings.getRetryCount();
    }
    return true;
  }

  /**
   * Remove listener queue by ID
   *
   * @param queueName Name of queue
   * @param id ID of listener container
   */
  public void removeDeclares(String queueName, String id) {
    SimpleMessageListenerContainer listener =
        (SimpleMessageListenerContainer) listenerMQRegistry.getListenerContainer(id);
    listener.removeQueueNames(queueName);
  }

  /**
   * Add listener queue by ID
   *
   * @param queueName Name of queue
   * @param id ID of listener container
   */
  public void initQueueListener(String queueName, String id) {
    SimpleMessageListenerContainer listener =
        (SimpleMessageListenerContainer) listenerMQRegistry.getListenerContainer(id);
    listener.addQueueNames(queueName);
  }
}
