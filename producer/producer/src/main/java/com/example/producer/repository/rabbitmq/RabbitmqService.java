package com.example.producer.repository.rabbitmq;

import com.example.producer.exception.ApiException;
import com.example.producer.utils.JsonUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Log4j2
@Service
public class RabbitmqService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public String sendRPCMessage(String exchange, String routingKey, Object message, String tokenKey) {
        log.info("Token [{}}] - Send message to exchange: {}, routingKey: {}, message: {}", tokenKey, exchange, routingKey, message);
        Message byteMessage = new Message(JsonUtils.writeValueAsBytes(message));
        Message response = rabbitTemplate.sendAndReceive(exchange, routingKey, byteMessage);
        if(response == null){
            log.error("[{}}] - No response received within timeout period", tokenKey);
            throw ApiException.ErrTimeout().message("No response received within timeout period").build();
        }
        return new String(response.getBody());
    };

    public void sendMessage(String exchange, String routingKey, Object message, String tokenKey) {
        log.info("Token [{}] - Send message to exchange: {}, routingKey: {}, message: {}", tokenKey, exchange, routingKey, message);
        try{
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
        } catch (AmqpException e) {
            log.error("Token [{}] - Send message failed: {}", tokenKey, e);
            throw ApiException.ErrInternal().build();
        }
    };
}
