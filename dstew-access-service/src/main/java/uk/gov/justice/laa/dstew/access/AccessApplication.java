package uk.gov.justice.laa.dstew.access;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring Boot microservice application.
 */
@SpringBootApplication
public class AccessApplication {

  /**
   * The application main method.
   *
   * @param args the application arguments.
   */
  public static void main(String[] args) {
    SpringApplication.run(AccessApplication.class, args);
  }
}
