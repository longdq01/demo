package com.example.producer.config;

import lombok.Getter;

import java.net.HttpURLConnection;

@Getter
public enum CodeResponse {
    INTERNAL(HttpURLConnection.HTTP_INTERNAL_ERROR, "ErrInternal"),
    BAD_REQUEST(HttpURLConnection.HTTP_BAD_REQUEST, "ErrBadRequest"),
    EXIST(HttpURLConnection.HTTP_BAD_REQUEST, "ErrExisted"),
    NOT_FOUND(HttpURLConnection.HTTP_NOT_FOUND, "ErrNotFound"),
    TIMEOUT(HttpURLConnection.HTTP_GATEWAY_TIMEOUT, "ErrTimeout"),
    ERR_INVALID_DATA(HttpURLConnection.HTTP_BAD_REQUEST, "ErrInvalidData");

    public final int code;
    public final String message;

    CodeResponse(int code, String message){
        this.code = code;
        this.message = message;
    }
}
