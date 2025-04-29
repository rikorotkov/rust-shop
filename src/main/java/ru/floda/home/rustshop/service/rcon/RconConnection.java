package ru.floda.home.rustshop.service.rcon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class RconConnection implements AutoCloseable {

    private final String host;
    private final int port;
    private final String password;
    private final int timeout;

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public RconConnection(String host, int port, String password, int timeout) throws IOException {
        this.host = host;
        this.port = port;
        this.password = password;
        this.timeout = timeout;
        connect();
    }

    private void connect() throws IOException {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), timeout);
            socket.setSoTimeout(timeout);

            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            // Аутентификация
            sendPacket(3, password);

            // Проверка ответа
            Packet authResponse = readPacket();
            if (authResponse.getRequestId() == -1) {
                throw new IOException("RCON authentication failed");
            }
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    public String sendCommand(String command) throws IOException {
        sendPacket(2, command);

        StringBuilder response = new StringBuilder();
        Packet packet;
        do {
            packet = readPacket();
            response.append(packet.getPayload());
        } while (packet.getRequestId() != -1 && packet.getPayload().length() > 0);

        return response.toString();
    }

    private void sendPacket(int type, String payload) throws IOException {
        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
        int requestId = ThreadLocalRandom.current().nextInt();

        ByteBuffer buffer = ByteBuffer.allocate(14 + payloadBytes.length)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(10 + payloadBytes.length)
                .putInt(requestId)
                .putInt(type)
                .put(payloadBytes)
                .put((byte) 0)
                .put((byte) 0);

        output.write(buffer.array());
        output.flush();
    }

    private Packet readPacket() throws IOException {
        int length = input.readInt();
        int requestId = input.readInt();
        int type = input.readInt();

        byte[] payloadBytes = new byte[length - 10];
        input.readFully(payloadBytes);
        input.readByte(); // Два нулевых байта в конце
        input.readByte();

        String payload = new String(payloadBytes, StandardCharsets.UTF_8);
        return new Packet(requestId, type, payload);
    }

    @Override
    public void close() {
        try {
            if (output != null) output.close();
            if (input != null) input.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            log.warn("Error closing RCON connection", e);
        }
    }

    @Getter
    @AllArgsConstructor
    private static class Packet {
        private final int requestId;
        private final int type;
        private final String payload;
    }
}
