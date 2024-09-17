package com.example.producer.controller;


import com.example.producer.model.BaseResponse;
import com.example.producer.model.dto.CreatePaymentReqDTO;
import com.example.producer.service.PaymentService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Log4j2
@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired
    PaymentService paymentService;

    @PostMapping
    public ResponseEntity<BaseResponse> createPayment(@Valid @RequestBody CreatePaymentReqDTO createPaymentReqDTO) {
        BaseResponse response = paymentService.createPayment(createPaymentReqDTO);
        return ResponseEntity.ok(response);
    }
}
