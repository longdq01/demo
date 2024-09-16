package com.example.producer.service;

import com.example.producer.model.BaseResponse;
import com.example.producer.model.dto.CreatePaymentReqDTO;

public interface PaymentService {
    BaseResponse createPayment(CreatePaymentReqDTO createPaymentReqDTO);
}
