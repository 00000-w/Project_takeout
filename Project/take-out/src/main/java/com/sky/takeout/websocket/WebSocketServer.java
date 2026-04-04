package com.sky.takeout.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/ws/{clientId}")
public class WebSocketServer {

    private static final Map<String, Session> SESSION_MAP
            = new ConcurrentHashMap<>();


    @OnOpen
    public void onOpen(Session session, @PathParam("clientId") String clientId) {
        log.info("WebSocket连接建立，clientId：{}", clientId);
        SESSION_MAP.put(clientId, session);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("clientId") String clientId) {
        log.info("收到客户端消息，clientId：{}，message：{}", clientId, message);
        if ("ping".equals(message)) {
            Session session = SESSION_MAP.get(clientId);
            if (session != null && session.isOpen()) {
                try {
                    session.getBasicRemote().sendText("pong");
                } catch (IOException e) {
                    log.error("回复心跳失败：", e);
                }
            }
        }
    }

    @OnClose
    public void onClose(@PathParam("clientId") String clientId) {
        log.info("WebSocket连接关闭，clientId：{}", clientId);
        SESSION_MAP.remove(clientId);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket发生错误：", error);
    }

    public void sendToAllClient(String message) {
        Collection<Session> sessions = SESSION_MAP.values();
        for (Session session : sessions) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                    log.info("推送消息给客户端，message：{}", message);
                } catch (IOException e) {
                    log.error("推送消息失败：", e);
                }
            }
        }
    }
}

