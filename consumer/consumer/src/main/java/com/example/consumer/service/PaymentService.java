package com.example.consumer.service;

import com.example.consumer.config.CodeResponse;
import com.example.consumer.config.Config;
import com.example.consumer.model.BaseResponse;
import com.example.consumer.model.ModelMapper;
import com.example.consumer.model.dto.CreatePaymentReqDTO;
import com.example.consumer.model.dto.PaymentPartnerDTO;
import com.example.consumer.model.entity.PaymentEntity;
import com.example.consumer.repository.mysql.PaymentRepository;
import com.example.consumer.utils.JsonUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RestClient restClient;

    @Autowired
    private Config config;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional
    @RabbitListener(queues = "${rabbitmq.payment-queue}")
    public void processPaymentRequest(@Payload String message) {
        CreatePaymentReqDTO createPaymentReqDTO = JsonUtils.parseJson(message, CreatePaymentReqDTO.class);
        log.info("Token [{}] - Begin process payment request, received message: {}", createPaymentReqDTO.getTokenKey(), createPaymentReqDTO);
        if (paymentRepository.existsById(createPaymentReqDTO.getOrderCode())) {
            log.info("Token [{}] - Payment entity is existed", createPaymentReqDTO.getTokenKey());
            BaseResponse response = BaseResponse.builder()
                    .code(CodeResponse.EXIST.getCode())
                    .message("Payment is existed")
                    .tokenKey(createPaymentReqDTO.getTokenKey())
                    .build();
            sendMessage(createPaymentReqDTO.getApiID(), response, createPaymentReqDTO.getTokenKey());
            return;
        }
        PaymentEntity paymentEntity = ModelMapper.paymentDTOToPaymentEntity(createPaymentReqDTO);
        log.info("Token [{}] - Save payment entity to db: {}", createPaymentReqDTO.getTokenKey(), paymentEntity);
        paymentRepository.save(paymentEntity);
        PaymentPartnerDTO partnerDTO = ModelMapper.paymentDTOToPaymentPartnerDTO(createPaymentReqDTO);
        log.info("Token [{}] - Send post request to partner api: {}", createPaymentReqDTO.getTokenKey(), partnerDTO);
        RestClient.ResponseSpec responseSpec = restClient.post()
                .uri(config.getApiXpartner())
                .contentType(MediaType.APPLICATION_JSON)
                .body(partnerDTO)
                .retrieve();
        ResponseEntity<Object> r = responseSpec.toEntity(Object.class);
        BaseResponse response = BaseResponse.builder()
                .code(r.getStatusCode().value())
                .message("Success")
                .tokenKey(createPaymentReqDTO.getTokenKey())
                .build();
        sendMessage(createPaymentReqDTO.getApiID(), response, createPaymentReqDTO.getTokenKey());
    }

    private void sendMessage(String routingKey, Object message, String tokenKey) {
        log.info("Token [{}] - Send message to default exchange with routingKey: {}, message: {}", tokenKey, routingKey, message);
        try {
            rabbitTemplate.convertAndSend(routingKey, message);
        } catch (AmqpException e) {
            log.error("Token [{}] - Send message failed: {}", tokenKey, e);
        }
    }
}
