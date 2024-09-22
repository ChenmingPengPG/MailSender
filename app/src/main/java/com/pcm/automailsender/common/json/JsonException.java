package com.pcm.automailsender.common.json;

public class JsonException extends RuntimeException {
    public JsonException(Throwable throwable) {
        super(throwable);
    }

    public JsonException(String detailMessage) {
        super(detailMessage);
    }
}
