package com.banking.internetbanking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class InternetBankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(InternetBankingApplication.class, args);
    }
}
