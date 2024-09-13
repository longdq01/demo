package com.example.producer.service;

import com.example.producer.model.CreatePaymentReqDTO;

public interface PaymentService {
    public void createPayment(CreatePaymentReqDTO createPaymentReqDTO);
}
