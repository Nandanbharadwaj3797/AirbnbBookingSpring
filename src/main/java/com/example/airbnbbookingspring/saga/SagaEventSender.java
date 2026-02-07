package com.example.airbnbbookingspring.saga;

public interface SagaEventSender {
    void send(String eventJson);
}
