package com.example.ems.config.rabbitmq;

import com.example.ems.config.redis.factory.YamlPropertySourceFactory;
import com.example.ems.dto.mq.QueueConf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "rabbit")
@PropertySource(value = "classpath:mq.yml", factory = YamlPropertySourceFactory.class)
public class RabbitMQSettings {
	private QueueConf user;
	private Long retryCount;
}
