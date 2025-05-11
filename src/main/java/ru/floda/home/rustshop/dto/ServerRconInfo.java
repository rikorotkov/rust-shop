package ru.floda.home.rustshop.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerRconInfo {

    private String rconIp;

    private int rconPort;

    private String rconPassword;

    public String buildWebSocketUrl() {
        return "ws://" + rconIp + ":" + rconPort + "/" + rconPassword;
    }

}
