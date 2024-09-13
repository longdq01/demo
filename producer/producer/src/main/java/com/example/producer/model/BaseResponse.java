package com.example.producer.model;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class BaseResponse {
    private int code;
    private String message;
    private Object data;
}
