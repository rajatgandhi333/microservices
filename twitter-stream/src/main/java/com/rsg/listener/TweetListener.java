package com.rsg.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusAdapter;

@Component
public class TweetListener extends StatusAdapter {

    private static final Logger LOG= LoggerFactory.getLogger(TweetListener.class);

    @Override
    public void onStatus(Status status){
        LOG.info("Status with {}", status.getText());
    }


}
