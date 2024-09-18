package com.example.producer.model.dto;

import lombok.Data;

@Data
public class CreatePaymentResDTO {
    private int code;
    private String message;
    private String tokenKey;
}
