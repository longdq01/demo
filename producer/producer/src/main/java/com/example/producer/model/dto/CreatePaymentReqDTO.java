package com.example.producer.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Data
public class CreatePaymentReqDTO {
    @JsonProperty("tokenKey")
    private String tokenKey;

    @JsonProperty("apiID")
    @NotBlank(message = "apiID must not be empty")
    private String apiID;

    @JsonProperty("mobile")
    @Pattern(regexp = "(0[3|5|7|8|9])+([0-9]{8})", message = "mobile number is invalid")
    private String mobile;

    @JsonProperty("bankCode")
    private String bankCode = "970445";

    @JsonProperty("accountNo")
    private String accountNo;

    @JsonProperty("payDate")
    @ValidDateTime(message = "payDate is invalid")
    private String payDate;

    @JsonProperty("additionalData")
    private String additionalData;

    @JsonProperty(value = "debitAmount")
    @Min(value = 0, message = "debitAmount must be greater than 0")
    @NotNull(message = "debitAmount must not be empty")
    private Integer debitAmount;

    @JsonProperty("respCode")
    private String respCode;

    @JsonProperty("respDesc")
    private String respDesc;

    @JsonProperty("traceTransfer")
    private String traceTransfer;

    @JsonProperty("messageType")
    private String messageType = "1";

    @JsonProperty("checkSum")
    private String checkSum;

    @JsonProperty("orderCode")
    @NotBlank(message = "orderCode must not be empty")
    private String orderCode;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("realAmount")
    private String realAmount;

    @JsonProperty("promotionCode")
    private String promotionCode;

    @JsonProperty("addValue")
    private String addValue;
}

@Constraint(validatedBy = DateTimeValidator.class)
@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@interface ValidDateTime {
    String message() default "Invalid date time format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

class DateTimeValidator implements ConstraintValidator<ValidDateTime, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        try {
            LocalDateTime t = LocalDateTime.parse(value, fmt);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
}

//{
//        "tokenKey": "1601353776839FT19310RH6P1",
//        "apiID": "restPayment",
//        "mobile": "0145225630",
//        "bankCode": "970445",
//        "accountNo": "0001100014211002",
//        "payDate": "20200929112923",
//        "addtionalData": "",
//        "debitAmount": 11200,
//        "respCode": "00",
//        "respDesc": "SUCCESS",
//        "traceTransfer": "FT19310RH6P1",
//        "messageType": "1",
//        "checkSum": "40e670720b754324af3d3a0ff49b52fb",
//        "orderCode": "FT19310RH6P1",
//        "userName": "cntest001",
//        "realAmount": "11200",
//        "promotionCode": "",
//        "addValue": "{\"payMethod\":\"01\",\"payMethodMMS\":1}"
//        }