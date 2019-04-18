package com.transaction.web.rabbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hua wb on 2018/12/11.
 */
@Configuration
public class RabbitConfig {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;


    public static final String EXCHANGE_DEFAULT = "exchange.default";


    public static final String QUEUE_DEFAULT = "queue.default";

    public static final String ROUTING_KEY_DEFAULT = "default";

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host,port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setConfirmCallback(new RabbitCallbackConfirm());
        rabbitTemplate.setReturnCallback(new RabbitCallbackReturn());
        return rabbitTemplate;
    }

    /**
     * directExchange 直连交换机
     * 根据routingKey一对一分发到指定队列
     * @return
     */
    @Bean
    public DirectExchange defaultExchange() {
        return new DirectExchange(EXCHANGE_DEFAULT);
    }

    /**
     * 普通队列：实时发送接收消息
     * @return
     */
    @Bean
    public Queue defaultQueue() {
        return new Queue(QUEUE_DEFAULT, true, false, false);
    }

    /**
     * 绑定交换机与队列
     * @param defaultQueue
     * @param defaultExchange
     * @return
     */
    @Bean
    public Binding bindingDefaultExchange(Queue defaultQueue, DirectExchange defaultExchange) {
        return BindingBuilder.bind(defaultQueue).to(defaultExchange).with(ROUTING_KEY_DEFAULT);
    }

}

