package com.challenge.fbiapp.services.db.impl;

import com.challenge.fbiapp.dao.MobstersDBDAO;
import com.challenge.fbiapp.model.Mobster;
import com.challenge.fbiapp.services.db.MobsterDBService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class MobsterDBServiceImpl implements MobsterDBService {


    private final MobstersDBDAO mobstersDBDAO;

    @Override
    public Mono<Mobster> getMobsterById(String mobsterId) {
        return mobstersDBDAO.getMobsterById(mobsterId);
    }

    @Override
    public Mono<Mobster> getBossById(String bossId) {
        return mobstersDBDAO.getBossById(bossId);
    }

    @Override
    public Mono<Mobster> imprison(String bossId) {
        return mobstersDBDAO.imprison(bossId);
    }

    @Override
    public Mono<Mobster> release(String bossId) {
        return mobstersDBDAO.release(bossId);
    }
}
