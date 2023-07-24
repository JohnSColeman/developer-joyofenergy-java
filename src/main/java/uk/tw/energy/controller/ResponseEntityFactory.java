package uk.tw.energy.controller;

import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface ResponseEntityFactory {

    default <T> ResponseEntity<Validation<Seq<String>, T>> responseEntity(Validation<Seq<String>, T> response) {
        return response.isValid() ? ResponseEntity.ok(response)
                : new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
