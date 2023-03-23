package com.oct.libraryeventsproducer.producer;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oct.libraryeventsproducer.domain.LibraryEvent;

@Component
@Slf4j
public class LibraryEventProducer {

  @Autowired
  KafkaTemplate<Integer, String> kafkaTemplate;

  @Autowired
  ObjectMapper objectMapper;

  public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
    Integer key = libraryEvent.getLibraryEventId();
    String value = objectMapper.writeValueAsString(libraryEvent);
    //we can also use send(topicName, key, value)
    ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.send("test_topic_kafka", key, value);
    listenableFuture.addCallback(new ListenableFutureCallback<>() {
      @Override
      public void onFailure(Throwable ex) {
        handleError(ex);
      }

      @Override
      public void onSuccess(SendResult<Integer, String> result) {
        handleSuccess(key, value, result);
      }
    });
  }

  private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
    log.error("Message sent successfully -> key: <{}>, value: <{}>, partition: <{}>", key, value, result.getRecordMetadata().partition());
  }

  private void handleError(Throwable ex) {
    log.info("Exception is <{}>", ex.getMessage());
  }
}
