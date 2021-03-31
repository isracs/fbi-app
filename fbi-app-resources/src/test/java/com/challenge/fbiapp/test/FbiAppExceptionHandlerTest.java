package com.challenge.fbiapp.test;

import com.challenge.fbiapp.error.MobsterError;
import com.challenge.fbiapp.error.MobsterErrorCodes;
import com.challenge.fbiapp.error.MobsterException;
import com.challenge.fbiapp.resources.controllers.exceptionhandler.FbiAppExceptionHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.ConstraintViolationException;
import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class FbiAppExceptionHandlerTest {

    private FbiAppExceptionHandler handler = new FbiAppExceptionHandler();

    @Test
    public void shouldMapAMobsterExceptionToResponseEntity() {
        // GIVEN
        MobsterException mobsterException = MobsterException.builder()
                .error(MobsterError.builder()
                        .code(MobsterErrorCodes.NOT_FOUND)
                        .message("The mobster was not found").build()
                ).httpStatus(HttpStatus.NOT_FOUND)
                .build();
        mobsterException.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        // WHEN
        ResponseEntity<MobsterError> response = handler
                .handleMobsterException(mobsterException);

        // THEN
        Assert.assertEquals(mobsterException.getHttpStatus(), response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(mobsterException.getError().getCode(), response.getBody().getCode());
        Assert.assertEquals(mobsterException.getError().getMessage(), response.getBody().getMessage());
    }

    @Test
    public void shouldMapConstraintExceptionToMobsterResponseEntity() {
        // GIVEN
        ConstraintViolationException constraintException =
                new ConstraintViolationException("The id of the mobster cannot be blank", Collections.emptySet());

        // WHEN
        ResponseEntity<MobsterError> response = handler
                .handleConstraintException(constraintException);

        // THEN
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(MobsterErrorCodes.BAD_REQUEST, response.getBody().getCode());
        Assert.assertEquals(constraintException.getMessage(), response.getBody().getMessage());
    }


}
