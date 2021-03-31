package com.challenge.fbiapp.db.dao.impl;

import com.challenge.fbiapp.dao.MobstersDBDAO;
import com.challenge.fbiapp.db.util.CourtOperationsHelper;
import com.challenge.fbiapp.db.util.MobsterDBHelper;
import com.challenge.fbiapp.model.Mobster;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class MobstersDBDAOImpl implements MobstersDBDAO {


    private MongoTemplate mongoOperator;

    @Autowired
    public void setMongoOperator(MongoTemplate mongoOperator) {
        this.mongoOperator = mongoOperator;
    }

    @Override
    public Mono<Mobster> getMobsterById(String mobsterNickname) {

        Mobster mobsterFound = MobsterDBHelper.getMobsterFromDB(mongoOperator, mobsterNickname);
        log.info("We have found this mobster! {}", mobsterFound);
        return Mono.just(mobsterFound);
    }

    @Override
    public Mono<Mobster> getBossById(String bossNickname) {
        Mobster mobsterFound = MobsterDBHelper.getPossibleBoss(mongoOperator, bossNickname);
        log.info("We have found this boss! {}", mobsterFound);
        return Mono.just(mobsterFound);
    }

    @Override
    @Transactional
    public Mono<Mobster> imprison(String bossNickname) {
        Mobster bossNowInPrison = CourtOperationsHelper.imprison(mongoOperator, bossNickname);
        log.info("The new mafia Boss is {} because The {} has been imprisoned", bossNowInPrison, bossNickname);
        return Mono.just(bossNowInPrison);
    }

    @Override
    @Transactional
    public Mono<Mobster> release(String bossNickname) {
        Mobster thisBossNowIsFree = CourtOperationsHelper.release(mongoOperator, bossNickname);
        log.info("The mafia Boss {} has been released", thisBossNowIsFree);
        return Mono.just(thisBossNowIsFree);
    }
}
