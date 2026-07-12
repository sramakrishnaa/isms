package com.isms.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class IsmsEurekaServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(IsmsEurekaServerApplication.class, args);
  }
}
