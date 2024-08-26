package com.example.runningservice.service;

import com.example.runningservice.dto.chat.MessageDeleteResponseDto;
import com.example.runningservice.dto.chat.MessageEditRequestDto;
import com.example.runningservice.dto.chat.MessageRequestDto;
import com.example.runningservice.dto.chat.MessageResponseDto;
import com.example.runningservice.enums.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketTest {

    protected StompSession stompSession;

    @LocalServerPort
    private int port;

    private final String url;

    private final WebSocketStompClient stompClient;

    public WebSocketTest() {
        this.stompClient = new WebSocketStompClient(new SockJsClient(createTransport()));
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        this.url = "ws://localhost:";
    }

    @BeforeEach
    public void connect() throws ExecutionException, InterruptedException, TimeoutException {
        this.stompSession = this.stompClient
            .connect(url + port + "/ws", new StompSessionHandlerAdapter() {})
            .get(3, TimeUnit.SECONDS);
    }

    @AfterEach
    public void disconnect() {
        if (this.stompSession.isConnected()) {
            this.stompSession.disconnect();
        }
    }

    private List<Transport> createTransport() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    private <T> CompletableFuture<T> subscribeToMessageResponse(Long roomId, Class<T> responseType) {
        CompletableFuture<T> responseFuture = new CompletableFuture<>();

        this.stompSession.subscribe("/topic/chatroom/" + roomId, new StompSessionHandlerAdapter() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return responseType;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                responseFuture.complete(responseType.cast(payload));
            }
        });

        return responseFuture;
    }

    @Test
    public void sendMessageAndReceive() throws Exception {
        CompletableFuture<MessageResponseDto> messageResponseFuture = subscribeToMessageResponse(1L, MessageResponseDto.class);

        MessageRequestDto messageRequestDto = MessageRequestDto.builder()
            .type(Message.TALK)
            .content("Hello")
            .imageUrl(null)
            .chatJoinId(1L)
            .build();

        this.stompSession.send("/app/send/1", messageRequestDto);

        MessageResponseDto response = messageResponseFuture.get(5, TimeUnit.SECONDS);

        assertNotNull(response);
        assertEquals(Message.TALK, response.getType());
        assertEquals("test", response.getSender());
        assertEquals("Hello", response.getContent());
    }

    @Test
    public void editMessageAndReceive() throws Exception {
        CompletableFuture<MessageResponseDto> messageResponseFuture = subscribeToMessageResponse(1L, MessageResponseDto.class);

        this.stompSession.send("/app/edit/1/1", new MessageEditRequestDto("World"));

        MessageResponseDto response = messageResponseFuture.get(5, TimeUnit.SECONDS);

        assertNotNull(response);
        assertEquals(Message.TALK, response.getType());
        assertEquals("test", response.getSender());
        assertEquals("World", response.getContent());
    }

    @Test
    public void deleteMessageAndReceive() throws Exception {
        CompletableFuture<MessageDeleteResponseDto> deleteConfirmationFuture = subscribeToMessageResponse(1L, MessageDeleteResponseDto.class);

        this.stompSession.send("/app/delete/1/1", null);

        MessageDeleteResponseDto response = deleteConfirmationFuture.get(5, TimeUnit.SECONDS);

        assertNotNull(response);
        assertEquals("메시지가 삭제되었습니다.", response.getMessage());
    }
}
