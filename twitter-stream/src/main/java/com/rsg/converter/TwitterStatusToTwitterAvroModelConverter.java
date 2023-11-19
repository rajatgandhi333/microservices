package com.rsg.converter;

import com.rsg.kafka.avro.model.TwitterAvroModel;
import org.springframework.stereotype.Component;
import twitter4j.Status;

@Component
public class TwitterStatusToTwitterAvroModelConverter {

    public TwitterAvroModel getTwitterAvroModelfromStatus(Status status) {
       return TwitterAvroModel.newBuilder()
                .setId(status.getId())
                .setText(status.getText())
                .setUserId(status.getUser().getId())
                .setCreatedAt(status.getCreatedAt().getTime())
                .build();
    }
}
