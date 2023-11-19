package com.rsg.ms.config;

import jdk.jfr.DataAmount;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties("twitter-stream")
public class TwitterStreamConfig {

    private List<String> twitterSearch;

    private String message;

}
