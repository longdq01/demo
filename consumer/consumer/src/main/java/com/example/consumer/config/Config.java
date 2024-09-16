package com.example.consumer.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class Config {
    @Value("${env}")
    private String env;

    @Value("${api.xpartner}")
    private String apiXpartner;
}
