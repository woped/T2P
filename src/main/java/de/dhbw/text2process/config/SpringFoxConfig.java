package de.dhbw.text2process.config;

import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringFoxConfig {
  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build()
        .apiInfo(apiInfo());
  }

  private ApiInfo apiInfo() {
    return new ApiInfo(
        "WoPeD - Text2Process Webservice",
        "This webservice can be used to translate text into a process model.",
        "3.8.0",
        null,
        new Contact(
            "Prof. Dr. Thomas Freytag",
            "https://woped.dhbw-karlsruhe.de/",
            "thomas.freytag@dhbw-karlsruhe.de"),
        "License",
        "https://github.com/tfreytag/T2P/blob/master/LICENSE.md",
        Collections.emptyList());
  }
}
