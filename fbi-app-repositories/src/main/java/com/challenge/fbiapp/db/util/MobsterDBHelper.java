package com.challenge.fbiapp.db.util;

import com.challenge.fbiapp.error.MobsterError;
import com.challenge.fbiapp.error.MobsterErrorCodes;
import com.challenge.fbiapp.error.MobsterException;
import com.challenge.fbiapp.model.Mobster;
import com.challenge.fbiapp.model.MobsterDBFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@Slf4j
public class MobsterDBHelper {


    public static Mobster getMobsterFromDB(MongoTemplate operator, String mobsterId) throws MobsterException {

        Query query = new Query(
                Criteria.where(MobsterDBFields.NICKNAME_FIELD).is(mobsterId));

        return Optional.of(
                operator.find(query, Mobster.class))
                .flatMap(theMobsterList ->
                        theMobsterList.stream().findFirst())
                .orElseThrow(() ->
                        getMobsterNotFoundException(mobsterId));
    }

    public static Mobster getTheOldestMobsterNotInJail(MongoTemplate operator) {

        Query query = new Query(
                Criteria.where(MobsterDBFields.IN_PRISON_FIELD).is(false));
        query.with(Sort.by(Sort.Direction.ASC, MobsterDBFields.MOBSTER_SINCE_FIELD));
        query.limit(1);

        return Optional.of(operator.find(query, Mobster.class))
                .flatMap(theMobsterList ->
                        theMobsterList.stream().findFirst())
                .orElseThrow(() ->
                        new RuntimeException("Unexpected Error"));
    }

    public static Mobster getPossibleBoss(MongoTemplate operator, String possibleBossId) {

        Query query = new Query(
                Criteria.where(MobsterDBFields.DIRECT_BOSS_FIELD).is(possibleBossId));

        Mobster theMobsterThatCanBeABoss = getMobsterFromDB(operator, possibleBossId);

        return Optional.of(
                operator.find(query, Mobster.class))
                .filter(MobsterDBHelper::isImportantEnough)
                .map(subordinatesOf -> {
                    log.info("So {} is a Mafia boss because he has {} subordinates"
                            , theMobsterThatCanBeABoss
                            , subordinatesOf.size());
                    return theMobsterThatCanBeABoss;
                })
                .orElseThrow(() ->
                        getIsNotABossException(theMobsterThatCanBeABoss));
    }

    private static MobsterException getMobsterNotFoundException(String mobsterId) {
        return new MobsterException(
                MobsterError.builder()
                        .code(MobsterErrorCodes.NOT_FOUND)
                        .message(String.format("The Mobster with nickname '%s' was not found", mobsterId)).build(),
                HttpStatus.NOT_FOUND);
    }

    private static MobsterException getIsNotABossException(Mobster theMobsterThatIsNotABoss) {
        return new MobsterException(
                MobsterError.builder()
                        .code(MobsterErrorCodes.NOT_A_BOSS)
                        .message(String.format("The Mobster %s is not a boss because it does not have enough subordinates",
                                theMobsterThatIsNotABoss)).build(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static boolean isImportantEnough(List<Mobster> theSubordinatesList) {
        return theSubordinatesList.size() >= 50;
    }
}
