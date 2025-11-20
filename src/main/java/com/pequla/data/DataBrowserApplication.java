package com.pequla.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DataBrowserApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataBrowserApplication.class, args);
    }

}
