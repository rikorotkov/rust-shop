package ru.floda.home.rustshop.rcon;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

@Slf4j
public class WebRcon extends WebSocketClient {

    public WebRcon(URI serverUri) {
        super(serverUri);
        this.setConnectionLostTimeout(0); // Отключаем проверку PING/PONG
    }
    //test
    @Override
    public void onOpen(ServerHandshake handshake) {
        log.info("Connection established");
        sendCommand("status"); // Тестовая команда
    }

    @Override
    public void onMessage(String message) {
        log.info("← " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("Closed: " + reason + " (code: " + code + ")");
    }

    @Override
    public void onError(Exception ex) {
        log.info("Error: " + ex.getMessage());
        ex.printStackTrace();
    }

    public void sendCommand(String command) {
        int identifier = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        String json = String.format(
                "{\"Identifier\":%d,\"Message\":\"%s\",\"Name\":\"WebRcon\"}",
                identifier,
                command
        );
        this.send(json);
    }

    public static void main(String[] args) throws Exception {
        WebRcon client = new WebRcon(new URI("ws://176.57.79.51:28016/admin"));

        // Таймаут подключения
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                if (!client.isOpen()) {
                    log.error("Timeout: Server not responding");
                    System.exit(1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        client.connectBlocking();

        // Держим соединение открытым
        while (client.isOpen()) {
            Thread.sleep(1000); // Чтобы программа не завершалась сразу
        }
    }
}