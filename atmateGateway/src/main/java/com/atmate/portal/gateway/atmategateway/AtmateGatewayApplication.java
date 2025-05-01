package com.atmate.portal.gateway.atmategateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class AtmateGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtmateGatewayApplication.class, args);
    }

}
