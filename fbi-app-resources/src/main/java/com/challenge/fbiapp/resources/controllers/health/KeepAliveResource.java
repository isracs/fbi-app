package com.challenge.fbiapp.resources.controllers.health;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/keepalive")
public class KeepAliveResource {

    private static final String KEEPALIVE_OK = "KEEPALIVE_OK";

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> keepAlive() {
        return ResponseEntity.ok(KEEPALIVE_OK);
    }
}
