package com.example.ems.config.messages;

import com.example.ems.dto.network.controller.Message;
import com.example.ems.config.redis.factory.YamlPropertySourceFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Slf4j
@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "message")
@PropertySource(value = "classpath:messages.yml", factory = YamlPropertySourceFactory.class)
public class Messages {
	private Message invalidData;
	private Message requestBodyIncorrect;
	private Message internalServerError;
	private Message resultEmpty;
	private Message notModified;
	private Message usernameUsed;
}
