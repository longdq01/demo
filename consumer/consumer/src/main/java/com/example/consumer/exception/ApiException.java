package com.example.consumer.exception;

import com.example.consumer.config.CodeResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ApiException extends RuntimeException {
    private CodeResponse code;
    private String message;

    public static ApiExceptionBuilder ErrInternal(){
        return new ApiExceptionBuilder().code(CodeResponse.INTERNAL);
    }

    public static ApiExceptionBuilder ErrExist() {
        return new ApiExceptionBuilder().code(CodeResponse.EXIST);
    }

    public static ApiExceptionBuilder ErrNotFound() {
        return new ApiExceptionBuilder().code(CodeResponse.NOT_FOUND);
    }

    public static ApiExceptionBuilder ErrInvalidData() {
        return new ApiExceptionBuilder().code(CodeResponse.ERR_INVALID_DATA);
    }

    public static ApiExceptionBuilder ErrTimeout(){
        return new ApiExceptionBuilder().code(CodeResponse.TIMEOUT);
    }

    public static ApiExceptionBuilder ErrBadRequest() {
        return new ApiExceptionBuilder().code(CodeResponse.BAD_REQUEST);
    }
}
