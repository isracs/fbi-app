package com.challenge.fbiapp.db.util;

import com.challenge.fbiapp.error.MobsterError;
import com.challenge.fbiapp.error.MobsterErrorCodes;
import com.challenge.fbiapp.error.MobsterException;
import com.challenge.fbiapp.model.Mobster;
import com.challenge.fbiapp.model.MobsterDBFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;

@Slf4j
public class CourtOperationsHelper {

    public static Mobster imprison(MongoTemplate operator, String bossId) {

        log.info("Let's to imprison a mobster called '{}'", bossId);
        Mobster mobsterToImprison = MobsterDBHelper.getMobsterFromDB(operator, bossId);

        if (mobsterToImprison.isInPrison()) {
            throw new MobsterException(MobsterError.builder()
                    .code(MobsterErrorCodes.ALREADY_IN_JAIL)
                    .message(String.format("The mobster %s is already in Jail", bossId))
                    .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }


        log.info("We found the mobster so, let's put him in Jail!");
        imprisonOrReleaseAMobster(operator, mobsterToImprison, true);

        log.info("Once in Jail, we have to figure out what is the oldest mobster will take car of the subordinates");
        Mobster theNewBoss = MobsterDBHelper.getTheOldestMobsterNotInJail(operator);

        log.info("So {} will be the new boss", theNewBoss);
        changeBosses(operator, mobsterToImprison, theNewBoss);

        log.info("We'll return the new boss with possibly some fields changed");
        return MobsterDBHelper.getMobsterFromDB(operator, theNewBoss.getNickname());
    }


    public static Mobster release(MongoTemplate operator, String bossId) {

        log.info("Let's to release a mobster from jail called '{}'", bossId);
        Mobster mobsterToRelease = MobsterDBHelper.getMobsterFromDB(operator, bossId);

        log.info("We found the mobster so, let's free him this time.");
        imprisonOrReleaseAMobster(operator, mobsterToRelease, false);

        log.info("So we have to return the subordinates to {} because he's active now", mobsterToRelease);
        assignFormerSubordinates(operator, mobsterToRelease);

        log.info("We'll return the new state of the mobster released");
        return MobsterDBHelper.getMobsterFromDB(operator, bossId);
    }

    private static void changeBosses(MongoTemplate operator, Mobster oldBoss, Mobster theNewBoss) {

        Query query = new Query(
                Criteria.where(MobsterDBFields.DIRECT_BOSS_FIELD).is(oldBoss.getNickname()));

        Update update = new Update();
        update.set(MobsterDBFields.DIRECT_BOSS_FIELD, theNewBoss.getNickname());
        update.push(MobsterDBFields.FORMER_BOSSES_FIELD, oldBoss.getNickname());
        operator.updateMulti(query, update, Mobster.class);

    }


    private static void assignFormerSubordinates(MongoTemplate operator, Mobster theFormerBoss) {

        Query query = new Query(
                Criteria.where(MobsterDBFields.FORMER_BOSSES_FIELD)
                        .is(theFormerBoss.getNickname()));

        Update update = new Update();
        update.set(MobsterDBFields.DIRECT_BOSS_FIELD, theFormerBoss.getNickname());
        update.pull(MobsterDBFields.FORMER_BOSSES_FIELD, theFormerBoss.getNickname());

        operator.updateMulti(query, update, Mobster.class);

    }

    private static void imprisonOrReleaseAMobster(MongoTemplate operator, Mobster thisGuy, boolean inPrison) {

        Query query = new Query(
                Criteria.where(MobsterDBFields.NICKNAME_FIELD).is(thisGuy.getNickname()));

        Update update = new Update();
        update.set(MobsterDBFields.IN_PRISON_FIELD, inPrison);

        operator.updateFirst(query, update, Mobster.class);
    }

}
