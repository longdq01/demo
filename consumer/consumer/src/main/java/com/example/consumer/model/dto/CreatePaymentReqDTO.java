package com.example.consumer.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreatePaymentReqDTO {
    @JsonProperty("tokenKey")
    private String tokenKey;

    @JsonProperty("apiID")
    private String apiID;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("bankCode")
    private String bankCode;

    @JsonProperty("accountNo")
    private String accountNo;

    @JsonProperty("payDate")
    private String payDate;

    @JsonProperty("additionalData")
    private String additionalData;

    @JsonProperty(value = "debitAmount")
    private Integer debitAmount;

    @JsonProperty("respCode")
    private String respCode;

    @JsonProperty("respDesc")
    private String respDesc;

    @JsonProperty("traceTransfer")
    private String traceTransfer;

    @JsonProperty("messageType")
    private String messageType;

    @JsonProperty("checkSum")
    private String checkSum;

    @JsonProperty("orderCode")
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
