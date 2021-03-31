package com.challenge.fbiapp.resources.controllers.mob;

import com.challenge.fbiapp.model.Mobster;
import com.challenge.fbiapp.services.db.MobsterDBService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mafia/mobster")
@Validated
public class MobsterResource {

    private final MobsterDBService mobsterDBService;

    @GetMapping("/{mobsterId}")
    public Mono<Mobster> getMobsterById(
            @PathVariable("mobsterId") @NotBlank @NotEmpty String mobsterId) {
        return mobsterDBService.getMobsterById(mobsterId);

    }
}
