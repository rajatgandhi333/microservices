package com.rsg.ms.exception;

import com.rsg.ms.client.KafkaAdminClient;

/**
 * Kafka Admin Exception handling file
 */
public class KafkaAdminClientException extends RuntimeException {

    public KafkaAdminClientException() {

    }

    public KafkaAdminClientException(String message) {
        super(message);
    }

    public KafkaAdminClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
