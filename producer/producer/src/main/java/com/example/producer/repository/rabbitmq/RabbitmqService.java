package com.example.producer.repository.rabbitmq;

import com.example.producer.exception.ApiException;
import com.example.producer.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class RabbitmqService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public String sendRPCMessage(String exchange, String routingKey, Object message) {
        log.info("[RabbitmqService - sendRPCMessage] exchange: {}, routingKey: {}, message: {}", exchange, routingKey, message);
        rabbitTemplate.setReturnsCallback(msg -> {
            log.info("[RabbitmqService - sendRPCMessage] return code = {}, text = {}", msg.getReplyCode(), msg.getReplyText());
        });
        Message byteMessage = new Message(JsonUtils.writeValueAsBytes(message));
        Message response = rabbitTemplate.sendAndReceive(exchange, routingKey, byteMessage);
        if(response == null){
            log.error("[RabbitmqService - sendRPCMessage] timeout reply rpc");
            throw ApiException.ErrTimeout().message("No response received within timeout period").build();
        }
        return new String(response.getBody());
    };
}
