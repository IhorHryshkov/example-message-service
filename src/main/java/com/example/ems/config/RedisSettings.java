package com.example.ems.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "redis")
public class RedisSettings {
    private Integer cacheTtl;
}
