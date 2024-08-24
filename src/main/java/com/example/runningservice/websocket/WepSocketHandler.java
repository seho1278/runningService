//package com.example.runningservice.websocket;
//
//import com.example.runningservice.entity.chat.MessageEntity;
//import com.example.runningservice.service.chat.ChatRoomService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class WepSocketHandler extends TextWebSocketHandler {
//
//    private final Set<WebSocketSession> sessions = new HashSet<>();
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        String payload = message.getPayload();
//        log.info("received message: " + payload);
//        broadcastMessage("New message: " + payload);
//    }
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        sessions.add(session);
//        log.info("connected: " + session.getId());
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        sessions.remove(session);
//        log.info("disconnected: " + session.getId());
//    }
//
//    private void broadcastMessage(String message) {
//        for (WebSocketSession session : sessions) {
//            try {
//                session.sendMessage(new TextMessage(message));
//            } catch (Exception e) {
//                log.error("Error sending message", e);
//            }
//        }
//    }
//
//    public void sendMessageToAll(String message) {
//        broadcastMessage(message);
//    }
//}
