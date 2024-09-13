package com.example.producer.controller;


import com.example.producer.model.BaseResponse;
import com.example.producer.model.CreatePaymentReqDTO;
import com.example.producer.service.PaymentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.HttpsURLConnection;

@Slf4j
@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired
    PaymentService paymentService;

    @PostMapping
    public ResponseEntity<BaseResponse> createPayment(@Valid @RequestBody CreatePaymentReqDTO createPaymentReqDTO) {
        paymentService.createPayment(createPaymentReqDTO);

        return ResponseEntity.ok(
                BaseResponse.builder()
                        .code(HttpsURLConnection.HTTP_OK)
                        .message("Success")
                        .data(createPaymentReqDTO)
                        .build()
        );
    }
}
