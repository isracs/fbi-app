package com.challenge.fbiapp.dao;

import com.challenge.fbiapp.model.Mobster;
import reactor.core.publisher.Mono;

public interface MobstersDBDAO {

    Mono<Mobster> getMobsterById(String mobsterNickname);

    Mono<Mobster> getBossById(String bossNickname);

    Mono<Mobster> imprison(String bossNickname);

    Mono<Mobster> release(String bossNickname);
}



