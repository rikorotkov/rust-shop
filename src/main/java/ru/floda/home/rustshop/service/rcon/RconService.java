package ru.floda.home.rustshop.service.rcon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.floda.home.rustshop.config.RconConfig;
import ru.floda.home.rustshop.model.Server;
import ru.floda.home.rustshop.model.ServerStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class RconService {

    private final RconConfig rconProperties;

    public RconResponse executeCommand(Server server, String command) {
        return executeCommand(server.getRconIp(),
                Integer.parseInt(server.getRconPort()),
                server.getRconPassword(),
                command);
    }

    public RconResponse executeCommand(String host, int port, String password, String command) {
        int retryCount = 0;
        Exception lastException = null;

        while (retryCount < rconProperties.getMaxRetries()) {
            try (RconConnection connection = new RconConnection(host, port, password, rconProperties.getTimeout())) {
                String response = connection.sendCommand(command);
                return new RconResponse(true, response);
            } catch (Exception e) {
                lastException = e;
                log.warn("RCON command failed (attempt {} of {}): {}",
                        retryCount + 1, rconProperties.getMaxRetries(), e.getMessage());

                if (retryCount < rconProperties.getMaxRetries() - 1) {
                    try {
                        Thread.sleep(rconProperties.getRetryDelay());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
                retryCount++;
            }
        }

        return new RconResponse(false, "Failed after " + rconProperties.getMaxRetries() +
                " attempts. Last error: " + lastException.getMessage());
    }

    public Integer getOnlinePlayers(Server server) {
        RconResponse response = executeCommand(server, "playerlist");
        if (!response.isSuccess()) {
            return null;
        }

        try {
            // Парсим ответ вида: "PlayerName (SteamID) 100.0.0.1:12345"
            String[] lines = response.getResponse().split("\n");
            return lines.length > 0 ? lines.length : 0;
        } catch (Exception e) {
            log.error("Failed to parse online players for server {}", server.getId(), e);
            return null;
        }
    }

    public ServerStatus checkServerStatus(Server server) {
        try {
            RconResponse response = executeCommand(server, "status");
            return response.isSuccess() ? ServerStatus.ONLINE : ServerStatus.OFFLINE;
        } catch (Exception e) {
            return ServerStatus.OFFLINE;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class RconResponse {
        private final boolean success;
        private final String response;
    }
}