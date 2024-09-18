package com.example.producer.repository.rabbitmq;


import com.example.producer.cache.CacheCreatePaymentResponse;
import com.example.producer.model.dto.CreatePaymentResDTO;
import com.example.producer.utils.JsonUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class RabbitmqWorker {
    @Autowired
    private CacheCreatePaymentResponse cacheCreatePaymentResponse;

    @RabbitListener(queues = "${service.api-id}")
    public void processCreatePaymentResponse(@Payload String message) {
        CreatePaymentResDTO response = JsonUtils.parseJson(message, CreatePaymentResDTO.class);
        log.info("Token [{}] - Add response to cache: {}", response.getTokenKey(), response);
        cacheCreatePaymentResponse.put(response.getTokenKey(), response);
    }
}
