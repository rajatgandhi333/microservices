package com.rsg.ms.service.impl;

import com.rsg.kafka.avro.model.TwitterAvroModel;
import com.rsg.ms.service.KafkaProducer;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class KafkaProducerImpl implements KafkaProducer<Long, TwitterAvroModel> {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaProducerImpl.class);

    private KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate;

    public KafkaProducerImpl(KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public void send(String topicName, Long key, TwitterAvroModel message) {
        LOG.info("Sending message {} to topic {}", message, topicName);
        CompletableFuture<SendResult<Long, TwitterAvroModel>> resultCompletableFuture = kafkaTemplate.send(topicName, key, message);
        resultCompletableFuture.whenComplete(getCallback(topicName, message));
    }

    private BiConsumer<SendResult<Long, TwitterAvroModel>, Throwable> getCallback(String topicName, TwitterAvroModel message) {
        return (result, ex) -> {
            if (ex == null) {
                RecordMetadata metadata = result.getRecordMetadata();
                LOG.info("Received metadata topic{},  Partition{}, Offset{}, Timestamp{}, at time{}",
                        metadata.topic(),
                        metadata.partition(),
                        metadata.partition(),
                        metadata.timestamp(),
                        System.nanoTime());
            } else {
                LOG.info("Error while sending message{} to topic {}", message.toString(), topicName, ex);
            }
        };
    }

    @PreDestroy
    private void terminate() {
        if (kafkaTemplate != null) {
            LOG.info("Terminating kafka producer");
            kafkaTemplate.destroy();
        }
    }
}
