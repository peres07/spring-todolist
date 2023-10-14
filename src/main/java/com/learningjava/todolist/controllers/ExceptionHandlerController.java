package com.learningjava.todolist.controllers;

import com.learningjava.todolist.response.ResponseModal;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseModal> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception, ResponseModal responseModal) {
        responseModal.setMessage(exception.getMostSpecificCause().getMessage());
        responseModal.setStatusCode(400);
        return ResponseEntity.status(400).body(responseModal);
    }

}
