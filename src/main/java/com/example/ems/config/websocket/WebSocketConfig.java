/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-09T23:08
 */
package com.example.ems.config.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Value("${parameters.sockets.callback.endpointsSocks}")
  private String[] endpointsSocks;

  @Value("${parameters.sockets.callback.destPrefixes}")
  private String[] destPrefixes;

  @Value("${parameters.sockets.callback.appPrefix}")
  private String appPrefix;

  @Value("${parameters.sockets.origins}")
  private String[] origins;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker(destPrefixes);
    config.setApplicationDestinationPrefixes(appPrefix);
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint(endpointsSocks).setAllowedOrigins(origins).withSockJS();
  }
}
