//package com.example.runningservice.websocket;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.core.TopicExchange;
//import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
//import org.springframework.amqp.support.converter.MessageConverter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RabbitmqConfig {
//
//    @Value("${spring.rabbitmq.host}")
//    private String host;
//
//    @Value("${spring.rabbitmq.username}")
//    private String username;
//
//    @Value("${spring.rabbitmq.password}")
//    private String password;
//
//    @Value("${spring.rabbitmq.port}")
//    private int port;
//
//    private static final String CHAT_QUEUE_NAME = "chat.queue";
//    private static final String CHAT_EXCHANGE_NAME = "chat.exchange";
//    private static final String ROUTING_KEY = "*.chatroom.*";
//
//
//    // Exchange 구성
//    @Bean
//    public TopicExchange topicExchange() {
//        return new TopicExchange(CHAT_EXCHANGE_NAME);
//    }
//
//    // Queue 구성
//    @Bean
//    public Queue queue() {
//        return new Queue(CHAT_QUEUE_NAME);
//    }
//
//    // queue와 exchange 바인딩
//    @Bean
//    public Binding binding(TopicExchange topicExchange, Queue queue) {
//        return BindingBuilder.bind(queue).to(topicExchange).with(ROUTING_KEY);
//    }
//
//    // rabbitmq와 연결을 할 connection factory 구성
//    @Bean
//    public ConnectionFactory connectionFactory() {
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        connectionFactory.setHost(host);
//        connectionFactory.setPort(port);
//        connectionFactory.setVirtualHost("/");
//        connectionFactory.setUsername(username);
//        connectionFactory.setPassword(password);
//        return connectionFactory;
//    }
//
//    // 메시지 전송하고 수신하기 위한 json 타입으로 메시지 변경
//    @Bean
//    public MessageConverter messageConverter() {
//        // LocalDateTime serializable
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
//        objectMapper.registerModule(new JavaTimeModule());
//
//        return new Jackson2JsonMessageConverter(objectMapper);
//    }
//
//    // connection factory, message converter를 통해 템플릿 구성
//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(messageConverter);
//        return rabbitTemplate;
//    }
//
//}
