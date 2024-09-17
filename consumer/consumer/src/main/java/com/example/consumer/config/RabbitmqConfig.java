package com.example.consumer.config;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.netty.channel.ChannelFactory;
import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.PooledChannelConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
        PooledChannelConnectionFactory pcf = new PooledChannelConnectionFactory(connectionFactory);
        pcf.setPoolConfigurer((pool, tx) -> {
            if (tx) {
                // configure the transactional pool
            }
            else {
                // configure the non-transactional pool
            }
        });
        return pcf;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(pooledChannelConnectionFactory());
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
