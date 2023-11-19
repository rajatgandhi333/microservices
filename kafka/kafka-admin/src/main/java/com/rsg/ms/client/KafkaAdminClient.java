package com.rsg.ms.client;


import com.rsg.ms.config.KafkaConfigData;
import com.rsg.ms.config.RetryDataConfig;
import com.rsg.ms.exception.KafkaAdminClientException;
import org.apache.kafka.clients.admin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class KafkaAdminClient {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaAdminClient.class);

    private final KafkaConfigData kafkaConfigData;

    private final AdminClient adminClient;

    private final RetryDataConfig retryDataConfig;

    private final RetryTemplate retryTemplate;

    private final WebClient webClient;

    public KafkaAdminClient(KafkaConfigData kafkaConfigData, AdminClient adminClient, RetryDataConfig retryDataConfig, RetryTemplate retryTemplate, WebClient webClient) {
        this.kafkaConfigData = kafkaConfigData;
        this.adminClient = adminClient;
        this.retryDataConfig = retryDataConfig;
        this.retryTemplate = retryTemplate;
        this.webClient = webClient;
    }

    public void createTopics() {
        CreateTopicsResult createTopicsResult;
        try {
            createTopicsResult = retryTemplate.execute(this::createTopic);
        } catch (Exception e) {
            throw new KafkaAdminClientException("Reached max number of Retries to create a kafka model");
        }
        checkTopicsCreated();
    }

    private CreateTopicsResult createTopic(RetryContext retryContext) {
        List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();

        List<NewTopic> topicList = topicNames.stream().map(
                topic -> new NewTopic(topic.trim(), kafkaConfigData.getNumOfPartitions(), kafkaConfigData.getReplicationFactor())
        ).collect(Collectors.toList());
        return adminClient.createTopics(topicList);
    }

    public void checkTopicsCreated() {
        Collection<TopicListing> topicListings = getTopics();
        int currentTry = 1;
        int maxretry = retryDataConfig.getMaxAttempts();
        Integer multiplier = retryDataConfig.getMultiplier().intValue();
        Long sleep = retryDataConfig.getSleepTimeMs();
        for (String currentTopic : kafkaConfigData.getTopicNamesToCreate()) {
            while (!isTopicExist(currentTopic, topicListings)) {
                checMaxRetry(maxretry, currentTry);
                sleepCheck(sleep);
                sleep += multiplier;
                topicListings = getTopics();
            }
        }
    }

    private void checMaxRetry(int maxretry, int currentTry) {
        if (currentTry > maxretry) {
            throw new KafkaAdminClientException("Exceeded max number of retries");
        }
    }

    private boolean isTopicExist(String currentTopic, Collection<TopicListing> topicListings) {
        if (currentTopic == null) return false;
        return topicListings.stream().anyMatch(topic -> topic.name().equals(currentTopic));
    }

    private void sleepCheck(Long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (Exception e) {
            throw new KafkaAdminClientException("Error while waiting for a topic");
        }
    }

    public void checkSchemaregistry() {
        int currentRetry = 1;
        int maxRetry = retryDataConfig.getMaxAttempts();
        int multiplier = retryDataConfig.getMultiplier().intValue();
        Long sleepTimer = retryDataConfig.getSleepTimeMs();
        while (getSchemaregistry().is2xxSuccessful()) {
            checMaxRetry(maxRetry, currentRetry++);
            sleepCheck(sleepTimer);
            sleepTimer += multiplier;
        }
    }

    private HttpStatusCode getSchemaregistry() {
        try {
            return webClient
                    .method(HttpMethod.GET)
                    .uri(kafkaConfigData.getSchemaRegistryUrl())
                    .exchangeToMono(clientResponse -> {
                        if (clientResponse.statusCode().is2xxSuccessful()) {
                            return Mono.just(clientResponse.statusCode());
                        } else {
                            return Mono.just(HttpStatus.SERVICE_UNAVAILABLE);
                        }
                    }).block();
        } catch (Exception e) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }

    }


    private Collection<TopicListing> getTopics() {
        Collection<TopicListing> topicListings;
        try {
            topicListings = retryTemplate.execute(this::getTopicListing);
        } catch (Throwable t) {
            throw new KafkaAdminClientException("Reached Max number of Retries", t);
        }
        return topicListings;
    }

    private Collection<TopicListing> getTopicListing(RetryContext retryContext) throws ExecutionException, InterruptedException {
        Collection<TopicListing> topicListings = adminClient.listTopics().listings().get();
        if (topicListings != null) {
            topicListings.forEach(topic -> LOG.info("The topic name is", topic.name()));
        }
        return topicListings;
    }

}
