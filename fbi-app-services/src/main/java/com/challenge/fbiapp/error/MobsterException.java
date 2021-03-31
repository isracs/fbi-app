package com.challenge.fbiapp.error;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobsterException extends RuntimeException {

    private MobsterError error;
    private HttpStatus httpStatus;

    public String getMessage() {
        return Optional.ofNullable(error)
            .map(MobsterError::getMessage)
            .orElseGet(super::getMessage);
    }
}