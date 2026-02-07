package com.example.airbnbbookingspring.saga;

public interface SagaEventReceiver {
    /**
     * Receives the next event JSON from the saga event queue, or null if none
     * available.
     * 
     * @return event JSON string or null
     */
    String receive();
}
