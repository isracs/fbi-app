package com.challenge.fbiapp.resources.controllers.exceptionhandler;

import com.challenge.fbiapp.error.MobsterError;
import com.challenge.fbiapp.error.MobsterErrorCodes;
import com.challenge.fbiapp.error.MobsterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

@Slf4j
@ControllerAdvice
public class FbiAppExceptionHandler {

    @ExceptionHandler(MobsterException.class)
    public ResponseEntity<MobsterError> handleMobsterException(MobsterException ex) {
        log.error(String.format("An exception has been caught! %s", ex.getMessage()), ex);
        return ResponseEntity.status(ex.getHttpStatus())
                .body(MobsterError.builder()
                        .code(ex.getError().getCode())
                        .message(ex.getError().getMessage())
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<MobsterError> handleConstraintException(ConstraintViolationException ex) {
        log.error(String.format("An exception has been caught! %s", ex.getMessage()), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(MobsterError.builder()
                        .code(MobsterErrorCodes.BAD_REQUEST)
                        .message("The id of the mobster cannot be blank")
                        .build());
    }

}
