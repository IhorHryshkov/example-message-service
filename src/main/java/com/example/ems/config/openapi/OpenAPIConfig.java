/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-06T16:49
 */
package com.example.ems.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

  private final BuildProperties buildProperties;

  @Value("${apiDocs.apiInfo.title}")
  private String title;

  @Value("${apiDocs.apiInfo.description}")
  private String description;

  @Value("${apiDocs.apiInfo.urlTermsOfService}")
  private String urlTermsOfService;

  @Value("${apiDocs.apiInfo.contact.name}")
  private String contactName;

  @Value("${apiDocs.apiInfo.contact.url}")
  private String contactUrl;

  @Value("${apiDocs.apiInfo.contact.email}")
  private String contactEmail;

  @Value("${apiDocs.apiInfo.license}")
  private String license;

  @Value("${apiDocs.apiInfo.licenseUrl}")
  private String licenseUrl;

  OpenAPIConfig(BuildProperties buildProperties) {
    this.buildProperties = buildProperties;
  }

  @Bean
  public OpenAPI docsOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title(title)
                .description(description)
                .version(buildProperties.getVersion())
                .termsOfService(urlTermsOfService)
                .contact(new Contact().email(contactEmail).name(contactName).url(contactUrl))
                .license(new License().name(license).url(licenseUrl)));
  }
}
