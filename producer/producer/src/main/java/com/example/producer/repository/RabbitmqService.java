package com.example.producer.repository;

import com.example.producer.exception.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;

@Slf4j
@Service
public class RabbitmqService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public String sendCorrelationMessage(String exchange, String routingKey, Object message) {
        log.info("[RabbitmqService - sendDirectMessage] exchange: {}, routingKey: {}, message: {}", exchange, routingKey, message);
        rabbitTemplate.setReturnsCallback(msg -> {
            log.info("[RabbitmqService - sendDirectMessage] return code = {}, text = {}", msg.getReplyCode(), msg.getReplyText());
        });
        ObjectMapper objectMapper = new ObjectMapper();
        Message byteMessage;
        try {
            byteMessage = new Message(objectMapper.writeValueAsBytes(message));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Message response = rabbitTemplate.sendAndReceive(exchange, routingKey, byteMessage);
        if(response == null){
            throw new ApiException(HttpsURLConnection.HTTP_GATEWAY_TIMEOUT, "Time out");
        }
        return new String(response.getBody());
    };
}
