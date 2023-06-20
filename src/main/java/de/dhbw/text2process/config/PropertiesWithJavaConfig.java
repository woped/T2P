package de.dhbw.text2process.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class PropertiesWithJavaConfig {
  public static String stanfordHost = "https://woped.dhbw-karlsruhe.de";

  public static String stanfordPort;

  public static String stanfordUri = "/t2p-stanford";

  public static String wordnetHost = "http://localhost";

  public static String wordnetPort = "5000";

  public static String worndnetUri = "baseform";
}
