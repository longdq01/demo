package com.example.consumer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class PaymentService {
    @RabbitListener(queues = "${rabbitmq.payment-queue}")
    public Object processPaymentRequest(@Payload String message) {
        log.info(message);
        return UUID.randomUUID().toString();
    }
}
