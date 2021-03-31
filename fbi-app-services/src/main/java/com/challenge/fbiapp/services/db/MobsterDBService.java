package com.challenge.fbiapp.services.db;

import com.challenge.fbiapp.model.Mobster;
import reactor.core.publisher.Mono;

public interface MobsterDBService {


    Mono<Mobster> getMobsterById(String mobsterId);

    Mono<Mobster> getBossById(String bossId);

    Mono<Mobster> imprison(String bossId);

    Mono<Mobster> release(String bossId);


}
