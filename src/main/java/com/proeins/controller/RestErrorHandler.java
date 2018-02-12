package com.proeins.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.proeins.exception.ShoeNotFoundException;

@ControllerAdvice
public class RestErrorHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RestErrorHandler.class);
	
	@ExceptionHandler(ShoeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleShoeNotFoundException(ShoeNotFoundException ex) {
        LOGGER.debug("handling 404 error on a todo entry");
        return ResponseEntity.notFound().build();
    }
	
	@ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        LOGGER.debug("Cannot modify id or article");
        return ResponseEntity.badRequest().body("Cannot modify id or article");
    }
	
}
