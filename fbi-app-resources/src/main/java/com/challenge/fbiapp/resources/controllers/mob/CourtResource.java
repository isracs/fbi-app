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

@RequiredArgsConstructor
@RestController
@RequestMapping("/mafia/court")
@Validated
public class CourtResource {

    private final MobsterDBService mobsterDBService;

    @GetMapping("/{bossId}/imprison")
    public Mono<Mobster> imprison(
            @PathVariable("bossId") @NotBlank @NotEmpty String bossId) {
        return mobsterDBService.imprison(bossId);
    }

    @GetMapping("/{bossId}/release")
    public Mono<Mobster> release(
            @PathVariable("bossId") @NotBlank @NotEmpty String bossId) {
        return mobsterDBService.release(bossId);
    }
}
