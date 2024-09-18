package com.example.consumer.exception;

import com.example.consumer.config.CodeResponse;
import com.example.consumer.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Autowired
    Config config;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException err, WebRequest request) {
        String message = (err.getMessage() != null) ? err.getMessage() : err.getCode().getMessage();
        ErrorResponse errorResponse = new ErrorResponse(err.getCode().code, message);

        if(config.getEnv().equals("dev") && isTraceOn(request)){
            errorResponse.setStackTrace(getStackTrace(err));
        }
        return ResponseEntity.status(err.getCode().code).body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(CodeResponse.ERR_INVALID_DATA.code, CodeResponse.ERR_INVALID_DATA.getMessage());
        if(config.getEnv().equals("dev")){
            errorResponse.setDevMessage(ex.getMessage());
            if(isTraceOn(request)){
                errorResponse.setStackTrace(getStackTrace(ex));
            }
        }
        for (FieldError fieldError: ex.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(),
                    fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(CodeResponse.ERR_INVALID_DATA.code).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception err, WebRequest request){
        ErrorResponse errorResponse = new ErrorResponse(CodeResponse.INTERNAL.code, CodeResponse.INTERNAL.getMessage());
        if(config.getEnv().equals("dev")){
            errorResponse.setDevMessage(err.getMessage());
            if(isTraceOn(request)){
                errorResponse.setStackTrace(getStackTrace(err));
            }
        }
        return ResponseEntity.status(CodeResponse.INTERNAL.code).body(errorResponse);
    }

    private boolean isTraceOn(WebRequest request) {
        String [] value = request.getParameterValues("trace");
        return Objects.nonNull(value)
                && value.length > 0
                && value[0].contentEquals("true");
    }

    private String getStackTrace(Exception err){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        err.printStackTrace(pw);
        return sw.toString();
    }
}
