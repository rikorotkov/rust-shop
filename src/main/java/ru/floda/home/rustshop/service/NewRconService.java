package ru.floda.home.rustshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.vv32.rcon.Rcon;
import org.springframework.stereotype.Service;
import ru.floda.home.rustshop.model.Server;
import ru.floda.home.rustshop.model.ServerStatus;
import ru.floda.home.rustshop.repository.ServerRepository;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewRconService {

    private final ServerRepository serverRepository;
    private static final int CONNECTION_TIMEOUT_MS = 5000;
    private static final int AUTH_TIMEOUT_MS = 3000;
    private static final int COMMAND_TIMEOUT_MS = 3000;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public void updateStatusServer(Server server) {
        // Используем final-переменные для хранения ресурсов
        final Socket[] socketHolder = new Socket[1];
        final Rcon[] rconHolder = new Rcon[1];

        Future<?> future = executor.submit(() -> {
            try {
                // 1. Установка соединения с таймаутом
                log.info("Connecting to {}:{} (timeout: {}ms)",
                        server.getRconIp(), server.getRconPort(), CONNECTION_TIMEOUT_MS);

                socketHolder[0] = new Socket();
                socketHolder[0].connect(new InetSocketAddress(
                                server.getRconIp(),
                                Integer.parseInt(server.getRconPort())),
                        CONNECTION_TIMEOUT_MS);

                // 2. Создание RCON соединения
                rconHolder[0] = Rcon.open(socketHolder[0].getInetAddress().getHostName(),
                        socketHolder[0].getPort());
                log.info("TCP connection established");

                // 3. Аутентификация с таймаутом
                log.info("Authenticating...");
                boolean authenticated = withTimeout(() ->
                                rconHolder[0].authenticate(server.getRconPassword()),
                        AUTH_TIMEOUT_MS, "Authentication");

                if (!authenticated) {
                    throw new IOException("Authentication failed");
                }
                log.info("Authentication successful");

                // 4. Тестовая команда с таймаутом
                log.info("Sending test command...");
                String response = withTimeout(() ->
                                rconHolder[0].sendCommand("status"),
                        COMMAND_TIMEOUT_MS, "Command execution");

                log.info("Command executed. Response length: {}", response.length());
                server.setStatus(ServerStatus.ONLINE);

            } catch (Exception e) {
                log.error("RCON error: {}", e.getMessage());
                server.setStatus(determineStatus(e));
            } finally {
                closeResources(rconHolder[0], socketHolder[0]);
            }

            serverRepository.save(server);
        });

        // Ожидаем завершения с общим таймаутом
        try {
            future.get(CONNECTION_TIMEOUT_MS + AUTH_TIMEOUT_MS + COMMAND_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            log.error("Overall RCON operation timed out");
            future.cancel(true);
            server.setStatus(ServerStatus.OFFLINE);
            serverRepository.save(server);
        } catch (Exception e) {
            log.error("Error in RCON operation: {}", e.getMessage());
        }
    }

    private <T> T withTimeout(Callable<T> callable, int timeoutMs, String operation) throws Exception {
        Future<T> future = executor.submit(callable);
        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new IOException(operation + " timed out after " + timeoutMs + "ms");
        }
    }

    private ServerStatus determineStatus(Exception e) {
        if (e instanceof IOException) {
            return ServerStatus.OFFLINE;
        } else if (e instanceof NumberFormatException) {
            return ServerStatus.UNKNOWN;
        } else {
            return ServerStatus.UNKNOWN;
        }
    }

    private void closeResources(Rcon rcon, Socket socket) {
        try {
            if (rcon != null) {
                rcon.close();
            }
        } catch (IOException e) {
            log.error("Error closing RCON: {}", e.getMessage());
        }

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            log.error("Error closing socket: {}", e.getMessage());
        }
    }
}