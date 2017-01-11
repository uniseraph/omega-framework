package com.omega.framework.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by jackychenb on 08/12/2016.
 */

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class TaskServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskServerApplication.class, args);
    }
}
