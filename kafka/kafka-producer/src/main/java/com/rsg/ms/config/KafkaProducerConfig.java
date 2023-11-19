package com.rsg.ms.config;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig<K extends Serializable, V extends SpecificRecordBase> {

    private final KafkaConfigData kafkaConfigData;

    private final KafkaProducerDataConfig kafkaProducerDataConfig;


    public KafkaProducerConfig(KafkaConfigData kafkaConfigData, KafkaProducerDataConfig kafkaProducerDataConfig) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaProducerDataConfig = kafkaProducerDataConfig;
    }


    @Bean
    public Map<String, Object> producerConfig() {
        Map<String, Object> map = new HashMap<>();
        map.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
        map.put(kafkaConfigData.getSchemaRegistryUrlKey(), kafkaConfigData.getSchemaRegistryUrl());
        map.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProducerDataConfig.getKeySerializerClass());
        map.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProducerDataConfig.getValueSerializerClass());
        map.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaProducerDataConfig.getBatchSize() * kafkaProducerDataConfig.getBatchSizeBoostFactor());
        map.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProducerDataConfig.getLingerMs());
        map.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, kafkaProducerDataConfig.getCompressionType());
        map.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProducerDataConfig.getRequestTimeoutMs());
        map.put(ProducerConfig.RETRIES_CONFIG, kafkaProducerDataConfig.getRetryCount());
        map.put(ProducerConfig.ACKS_CONFIG, kafkaProducerDataConfig.getAcks());
        return map;
    }

    @Bean
    public ProducerFactory<K, V> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<K, V> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
