package com.example.ems.config.rabbitmq;

import lombok.AllArgsConstructor;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.rabbit.config.RabbitListenerConfigUtils;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class RabbitMQConfig {

  private RabbitMQSettings rabbitMQSettings;

  @Bean
  public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
    final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(jsonMessageConverter());
    return rabbitTemplate;
  }

  @Bean
  public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
    RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
    rabbitAdmin.declareExchange(
        ExchangeBuilder.directExchange(this.rabbitMQSettings.getUserAdd().getExchange()).build());
    rabbitAdmin.declareExchange(
        ExchangeBuilder.directExchange(this.rabbitMQSettings.getWebsocket().getExchange()).build());
    rabbitAdmin.declareExchange(
        ExchangeBuilder.directExchange(this.rabbitMQSettings.getUserUpdate().getExchange())
            .build());
    rabbitAdmin.declareExchange(
        ExchangeBuilder.directExchange(this.rabbitMQSettings.getCounterAdd().getExchange())
            .build());
    return rabbitAdmin;
  }

  @Bean(name = RabbitListenerConfigUtils.RABBIT_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME)
  public RabbitListenerEndpointRegistry defaultRabbitListenerEndpointRegistry() {
    return new RabbitListenerEndpointRegistry();
  }
}
