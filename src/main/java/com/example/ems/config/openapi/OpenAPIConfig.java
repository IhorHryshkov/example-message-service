/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-06T16:49
 */
package com.example.ems.config.openapi;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableOpenApi
@Import(BeanValidatorPluginsConfiguration.class)
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

  @Value("${apiDocs.paths}")
  private String paths;

  OpenAPIConfig(BuildProperties buildProperties) {
    this.buildProperties = buildProperties;
  }

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.OAS_30)
        .select()
        .paths(PathSelectors.ant(paths))
        .build()
        .apiInfo(apiInfo())
        .useDefaultResponseMessages(false);
  }

  private ApiInfo apiInfo() {
    return new ApiInfo(
        title,
        description,
        buildProperties.getVersion(),
        urlTermsOfService,
        new Contact(contactName, contactUrl, contactEmail),
        license,
        licenseUrl,
        Collections.emptyList());
  }
}
