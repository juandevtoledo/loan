package com.lulobank.credits.starter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

  private static final String HTTPURL = "https://github.com/piso19";

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
            .useDefaultResponseMessages(false)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
            .apiInfo(getApiInfo());
  }

  private ApiInfo getApiInfo() {

    final Contact contactInfo = new Contact("Lulo Bank", HTTPURL, "tbd@lulobank.com");
    return new ApiInfoBuilder()
            .title("API Rest loan microservice")
            .description("Api docs for loan microservice.")
            .version("0.0.0")
            .termsOfServiceUrl(HTTPURL)
            .contact(contactInfo)
            .license("Copyright")
            .licenseUrl(HTTPURL)
            .build();
  }

  @Bean
  @Profile("test")
  static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
    PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
    configurer.setIgnoreUnresolvablePlaceholders(true);
    configurer.setOrder(configurer.getOrder() - 1);
    return configurer;
  }

}
