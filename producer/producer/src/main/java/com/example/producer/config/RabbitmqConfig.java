package com.example.producer.config;


import com.rabbitmq.client.ConnectionFactory;
import lombok.Data;
import lombok.Setter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.PooledChannelConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// https://docs.spring.io/spring-amqp/reference/amqp/connections.html
@Data
@Configuration
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitmqConfig {
    private String host;
    private int port;
    private String username;
    private String password;

    private String paymentQueue;
    private String paymentExchange;
    private String paymentRoutingKey;
    private String replyQueue;

    @Bean
    public Declarables declarables(){
        Queue paymentQ = new Queue(paymentQueue, true, false, true);
        Binding paymentBinding = new Binding(paymentQueue, Binding.DestinationType.QUEUE,
                paymentExchange, paymentRoutingKey, null);
        DirectExchange paymentX = new DirectExchange(paymentExchange);
        return new Declarables(paymentQ, paymentX, paymentBinding);
    }

    @Bean
    public PooledChannelConnectionFactory pooledChannelConnectionFactory() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return new PooledChannelConnectionFactory(connectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(pooledChannelConnectionFactory());
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setReplyAddress(replyQueue().getName());
        rabbitTemplate.setReplyTimeout(30000);
        return rabbitTemplate;
    }

    @Bean
    public SimpleMessageListenerContainer replyListenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(pooledChannelConnectionFactory());
        container.setQueues(replyQueue());
        container.setMessageListener(rabbitTemplate());
        return container;
    }

    @Bean
    public Queue replyQueue() {
        return new Queue(replyQueue);
    }
}
