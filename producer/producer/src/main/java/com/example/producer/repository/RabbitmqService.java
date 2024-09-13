package com.example.producer.repository;

import com.example.producer.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;

@Slf4j
@Service
public class RabbitmqService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public Object sendCorrelationMessage(String exchange, String routingKey, Object message) {
        log.info("[RabbitmqService - sendDirectMessage] exchange: {}, routingKey: {}, message: {}", exchange, routingKey, message);
        rabbitTemplate.setReturnsCallback(msg -> {
            log.info("[RabbitmqService - sendDirectMessage] return code = {}, text = {}", msg.getReplyCode(), msg.getReplyText());
        });

        Object response = rabbitTemplate.convertSendAndReceive(exchange, routingKey, message);
        if(response == null){
            throw new ApiException(HttpsURLConnection.HTTP_GATEWAY_TIMEOUT, "Time out");
        }
        return response;
    };
}
