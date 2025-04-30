package ru.floda.home.rustshop.service.rcon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class RconConnection implements AutoCloseable {
    private static final int PACKET_HEADER_SIZE = 12;
    private static final int MAX_PACKET_SIZE = 4096;

    private static final int AUTH_RESPONSE_TIMEOUT = 5000;
    private static final int RESPONSE_TIMEOUT = 10000;

    private final String host;
    private final int port;
    private final String password;
    private final int timeout;

    private Socket socket;
    private InputStream input;
    private OutputStream output;
    private int lastRequestId;

    public RconConnection(String host, int port, String password, int timeout) throws IOException {
        this.host = host;
        this.port = port;
        this.password = password;
        this.timeout = timeout;
        connect();
    }

    private void connect() throws IOException {
        try {
            log.info("Connecting to " + host + ":" + port);
            socket = new Socket();
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(AUTH_RESPONSE_TIMEOUT);

            // Увеличиваем буферы
            socket.setReceiveBufferSize(8192);
            socket.setSendBufferSize(8192);

            socket.connect(new InetSocketAddress(host, port), timeout);

            input = new BufferedInputStream(socket.getInputStream());
            output = new BufferedOutputStream(socket.getOutputStream());

            // Аутентификация
            lastRequestId = 1; // Фиксированный ID для auth
            sendPacket(3, password);

            // Ждём ответ специально дольше для аутентификации
            socket.setSoTimeout(AUTH_RESPONSE_TIMEOUT);
            Packet authResponse = readPacket();

            if (authResponse.getRequestId() == -1) {
                throw new IOException("RCON authentication failed: Invalid password");
            }

            // Устанавливаем нормальный таймаут для команд
            socket.setSoTimeout(RESPONSE_TIMEOUT);
            log.info("Successfully connected to RCON at {}:{}", host, port);

        } catch (SocketTimeoutException e) {
            close();
            throw new IOException("RCON connection timeout. Check if server is running and ports are open", e);
        } catch (IOException e) {
            close();
            throw new IOException("RCON connection failed: " + e.getMessage(), e);
        }
    }

    public String sendCommand(String command) throws IOException {
        lastRequestId = ThreadLocalRandom.current().nextInt();
        sendPacket(2, command);

        StringBuilder response = new StringBuilder();
        Packet packet;
        int attempts = 0;

        do {
            packet = readPacket();
            if (packet.getRequestId() == lastRequestId) {
                response.append(packet.getPayload());
            }
            attempts++;
        } while (attempts < 10 && packet.getRequestId() != -1);

        return response.toString();
    }

    private void sendPacket(int type, String payload) throws IOException {
        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
        int packetSize = PACKET_HEADER_SIZE + payloadBytes.length + 2;

        ByteBuffer buffer = ByteBuffer.allocate(packetSize)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(packetSize - 4)
                .putInt(lastRequestId)
                .putInt(type)
                .put(payloadBytes)
                .put((byte)0)
                .put((byte)0);

        output.write(buffer.array());
        output.flush();
    }

    private Packet readPacket() throws IOException {
        byte[] sizeBytes = new byte[4];
        readFully(input, sizeBytes);
        int size = ByteBuffer.wrap(sizeBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();

        if (size > MAX_PACKET_SIZE) {
            throw new IOException("Packet too large: " + size);
        }

        byte[] packetData = new byte[size];
        readFully(input, packetData);

        ByteBuffer buffer = ByteBuffer.wrap(packetData).order(ByteOrder.LITTLE_ENDIAN);
        int requestId = buffer.getInt();
        int type = buffer.getInt();

        byte[] payloadBytes = new byte[size - 10];
        buffer.get(payloadBytes);

        return new Packet(requestId, type, new String(payloadBytes, StandardCharsets.UTF_8));
    }

    private void readFully(InputStream in, byte[] buffer) throws IOException {
        int bytesRead = 0;
        while (bytesRead < buffer.length) {
            int result = in.read(buffer, bytesRead, buffer.length - bytesRead);
            if (result == -1) {
                throw new EOFException("End of stream reached");
            }
            bytesRead += result;
        }
    }

    @Override
    public void close() {
        try {
            if (output != null) output.close();
            if (input != null) input.close();
            if (socket != null && !socket.isClosed()) socket.close();
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
