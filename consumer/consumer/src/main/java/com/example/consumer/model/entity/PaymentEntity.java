package com.example.consumer.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment")
public class PaymentEntity {
    @Id
    @Column(name = "order_code")
    private String orderCode;

    @Column(name = "token_key")
    private String tokenKey;

    @Column(name = "api_id")
    private String apiID;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "pay_date")
    private String payDate;

    @Column(name = "additional_data")
    private String additionalData;

    @Column(name = "debit_amount")
    private Integer debitAmount;

    @Column(name = "resp_code")
    private String respCode;

    @Column(name = "resp_desc")
    private String respDesc;

    @Column(name = "trace_transfer")
    private String traceTransfer;

    @Column(name = "message_type")
    private String messageType;

    @Column(name = "check_sum")
    private String checkSum;

    @Column(name = "user_name")
    private String username;

    @Column(name = "real_amount")
    private Integer realAmount;

    @Column(name = "promotion_code")
    private String promotionCode;

    @Column(name = "add_value")
    private String addValue;
}
