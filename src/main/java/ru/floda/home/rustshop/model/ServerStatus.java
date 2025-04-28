package ru.floda.home.rustshop.model;

import lombok.Getter;

@Getter
public enum ServerStatus {
    ONLINE("Online"),
    OFFLINE("Offline"),
    UNKNOWN("Unknown");

    private final String displayName;

    ServerStatus(String displayName) {
        this.displayName = displayName;
    }

}
