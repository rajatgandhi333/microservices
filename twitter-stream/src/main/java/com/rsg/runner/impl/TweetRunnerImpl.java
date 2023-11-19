package com.rsg.runner.impl;


import com.rsg.listener.TweetListener;
import com.rsg.ms.config.TwitterStreamConfig;
import com.rsg.runner.TweetRunner;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

@Component
public class TweetRunnerImpl implements TweetRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TweetRunnerImpl.class);

    private final TwitterStreamConfig twitterStreamConfig;

    private final TweetListener tweetListener;

    private TwitterStream twitterStream;

    public TweetRunnerImpl(TwitterStreamConfig twitterStreamConfig, TweetListener tweetListener) {
        this.twitterStreamConfig = twitterStreamConfig;
        this.tweetListener = tweetListener;
    }

    @PreDestroy
    public void close() {
        if (twitterStream != null) {
            LOG.info("closing down");
            twitterStream.shutdown();
        }
    }


    @Override
    public void start() throws TwitterException {
        twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(tweetListener);
        String[] search = twitterStreamConfig.getTwitterSearch().toArray(new String[0]);
        twitterStream.filter(search);
        LOG.info("filter started" + search);
    }
}
