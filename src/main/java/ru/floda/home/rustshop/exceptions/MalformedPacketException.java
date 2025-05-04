package ru.floda.home.rustshop.exceptions;

public class MalformedPacketException extends RuntimeException {
    public MalformedPacketException(String message) {
        super(message);
    }
}
