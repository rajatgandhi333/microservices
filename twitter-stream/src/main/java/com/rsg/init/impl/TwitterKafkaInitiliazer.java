package com.rsg.init.impl;

import com.rsg.init.StreamInitialzier;
import com.rsg.ms.client.KafkaAdminClient;
import com.rsg.ms.config.KafkaConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TwitterKafkaInitiliazer implements StreamInitialzier {

    private static Logger LOG = LoggerFactory.getLogger(TwitterKafkaInitiliazer.class);

    private final KafkaConfigData kafkaConfigData;

    private final KafkaAdminClient kafkaAdminClient;

    public TwitterKafkaInitiliazer(KafkaConfigData kafkaConfigData, KafkaAdminClient kafkaAdminClient) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaAdminClient = kafkaAdminClient;
    }

    @Override
    public void init() {
        kafkaAdminClient.createTopics();
        kafkaAdminClient.checkSchemaregistry();
        LOG.info("Topic with name {} is ready  ", kafkaConfigData.getTopicNamesToCreate().toArray());
    }
}
