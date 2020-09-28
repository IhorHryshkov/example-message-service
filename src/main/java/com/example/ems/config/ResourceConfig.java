/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-13T19:24
 */
package com.example.ems.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class ResourceConfig implements WebMvcConfigurer {

  @Value("${parameters.sockets.callback.endpointsCallbackDock}")
  private String endpointsCallbackDock;

  @Value("${parameters.sockets.callback.resourcesCallbackDock}")
  private String resourcesCallbackDock;

  @Value("${apiDocs.endpointsRestapiDock}")
  private String endpointsRestDock;

  @Value("${server.cors.mapping}")
  private String mapping;

  @Value("${server.cors.origins}")
  private String[] origins;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping(mapping).allowedOrigins(origins);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler(String.format("%s/**", endpointsCallbackDock))
        .addResourceLocations(resourcesCallbackDock);
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry
        .addViewController(String.format("%s/", endpointsCallbackDock))
        .setViewName(String.format("forward:%s/index.html", endpointsCallbackDock));
    registry
        .addRedirectViewController(
            endpointsCallbackDock, String.format("%s/", endpointsCallbackDock))
        .setKeepQueryParams(true)
        .setStatusCode(HttpStatus.PERMANENT_REDIRECT);
    registry
        .addRedirectViewController(endpointsRestDock, String.format("%s/", endpointsRestDock))
        .setKeepQueryParams(true)
        .setStatusCode(HttpStatus.PERMANENT_REDIRECT);
  }
}
