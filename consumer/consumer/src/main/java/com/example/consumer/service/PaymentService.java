package com.example.consumer.service;

import com.example.consumer.config.Config;
import com.example.consumer.model.BaseResponse;
import com.example.consumer.model.ModelMapper;
import com.example.consumer.model.dto.CreatePaymentReqDTO;
import com.example.consumer.repository.mysql.PaymentRepository;
import com.example.consumer.utils.JsonUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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

    @Transactional
    @RabbitListener(queues = "${rabbitmq.payment-queue}")
    public String processPaymentRequest(@Payload String message){
        log.info("[PaymentService - processPaymentRequest] received message: {}", message);
        CreatePaymentReqDTO createPaymentReqDTO = JsonUtils.parseJson(message, CreatePaymentReqDTO.class);
        log.info("[PaymentService - processPaymentRequest] paymentDTO: {}", createPaymentReqDTO);
//        if (paymentRepository.existsById(createPaymentReqDTO.getOrderCode())) {
//            return JsonUtils.stringifyJson(BaseResponse.builder()
//                    .code(CodeResponse.EXIST.getCode())
//                    .message("payment is existed")
//                    .data(null)
//                    .build());
//        }
        paymentRepository.save(ModelMapper.paymentDTOToPaymentEntity(createPaymentReqDTO));

        RestClient.ResponseSpec responseSpec = restClient.post()
                .uri(config.getApiXpartner())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ModelMapper.paymentDTOToPaymentPartnerDTO(createPaymentReqDTO))
                .retrieve();
        ResponseEntity<Object> r = responseSpec.toEntity(Object.class);
        return JsonUtils.stringifyJson(BaseResponse.builder()
                        .code(r.getStatusCode().value())
                        .data(r.getBody())
                .build());
    }
}
