package com.oct.libraryeventsproducer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oct.libraryeventsproducer.domain.LibraryEvent;
import com.oct.libraryeventsproducer.domain.LibraryEventType;
import com.oct.libraryeventsproducer.producer.LibraryEventProducer;

@RestController
public class LibraryEventsController {

  @Autowired
  LibraryEventProducer libraryEventProducer;

  @PostMapping("v1/library-events")
  public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody LibraryEvent libraryEvent)
      throws JsonProcessingException {
    libraryEvent.setLibraryEventType(LibraryEventType.ADD);
    libraryEventProducer.sendLibraryEvent(libraryEvent);
    return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
  }

  @PutMapping("v1/library-events")
  public ResponseEntity<?> putLibraryEvent(@RequestBody LibraryEvent libraryEvent)
      throws JsonProcessingException {
    if (libraryEvent.getLibraryEventId() == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass libraryEventId");
    }
    libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
    libraryEventProducer.sendLibraryEvent(libraryEvent);
    return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
  }
}
