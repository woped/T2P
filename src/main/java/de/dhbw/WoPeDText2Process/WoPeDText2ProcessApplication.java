package de.dhbw.WoPeDText2Process;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
public class WoPeDText2ProcessApplication {

	public static void main(String[] args) {
		SpringApplication.run(WoPeDText2ProcessApplication.class, args);
	}

}
