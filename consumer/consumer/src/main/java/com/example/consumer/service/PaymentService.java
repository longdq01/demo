package com.example.consumer.service;

import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class PaymentService {
    @RabbitListener(queues = "${rabbitmq.payment-queue}")
    public String processPaymentRequest(@Payload String message) {
        log.info(message);
        return "Abc";
    }
}
