package com.example.runningservice.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//    @Value("${spring.stomp.relay.host}")
//    private String host;
//
//    @Value("${spring.stomp.relay.port}")
//    private int port;
//
//    @Value("${spring.stomp.relay.system-login}")
//    private String login;
//
//    @Value("${spring.stomp.relay.system-passcode}")
//    private String passcode;


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setPathMatcher(new AntPathMatcher("."));
        registry.enableSimpleBroker("/topic");
//        registry.enableStompBrokerRelay("/topic", "/queue", "/exchange", "/amq/queue")
//                .setRelayHost(host)
//                .setRelayPort(port)
//                .setSystemLogin(login)
//                .setSystemPasscode(passcode)
//                .setClientLogin(login)
//                .setClientPasscode(passcode);
//        registry.setApplicationDestinationPrefixes("/pub");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*");
    }
}
