package com.interview.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.interview.platform.repository")
@ComponentScan(basePackages = "com.interview.platform")
public class InterviewPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterviewPlatformApplication.class, args);
    }

}
