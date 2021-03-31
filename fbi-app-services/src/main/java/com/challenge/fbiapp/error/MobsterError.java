package com.challenge.fbiapp.error;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MobsterError {

    @NonNull
    private String code;
    @NonNull
    private String message;

}
