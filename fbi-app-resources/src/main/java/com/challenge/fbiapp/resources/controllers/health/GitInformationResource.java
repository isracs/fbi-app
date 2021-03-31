package com.challenge.fbiapp.resources.controllers.health;

import com.challenge.fbiapp.resources.controllers.payloads.GitInformationPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
public class GitInformationResource {

    private final GitInformationPayload gitInformationPayload;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/gitinformation", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<GitInformationPayload> getGitInformation() {
        return Mono.just(gitInformationPayload);
    }

}

