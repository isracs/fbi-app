package com.challenge.fbiapp.respositories.test;


import com.challenge.fbiapp.db.dao.impl.MobstersDBDAOImpl;
import com.challenge.fbiapp.error.MobsterException;
import com.challenge.fbiapp.model.Mobster;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static com.challenge.fbiapp.model.MobsterDBFields.*;
import static com.challenge.fbiapp.respositories.test.util.RepositoriesTestHelper.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MobstersDBDAOImplTest {

    private static String AN_ID = "AN_ID";

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private MobstersDBDAOImpl underTest;


    @Test
    public void getAMobsterById() {
        //GIVEN
        Mobster aMockedMobster = getAMockedMobster(false);

        //WHEN
        when(mongoTemplate.find(any(Query.class), eq(Mobster.class))).thenReturn(Collections.singletonList(aMockedMobster));

        //THEN
        Mobster bossById = underTest.getMobsterById(AN_ID).block();
        assertOkMobster(aMockedMobster, bossById);
    }

    @Test(expected = MobsterException.class)
    public void getAMobsterNotFoundException() {
        //GIVEN
        MobsterException expectedException = getAMobsterNotFoundExceptionWhenExecuteFind(AN_ID);

        //WHEN
        when(mongoTemplate.find(any(Query.class), eq(Mobster.class))).thenReturn(Collections.emptyList());

        //THEN
        Mono<Mobster> aResponse = underTest.getMobsterById(AN_ID);
        try {
            aResponse.block();
        } catch (MobsterException actualException) {
            assertException(expectedException, actualException);
            throw expectedException;
        }
    }

    @Test
    public void getABossById() {
        //GIVEN
        Mobster aBoss = getAMockedMobster(false);

        Query queryForSubordinates = new Query(Criteria.where(DIRECT_BOSS_FIELD).is(AN_ID));
        Query queryForMobster = new Query(Criteria.where(NICKNAME_FIELD).is(AN_ID));


        //WHEN
        //For the filter that checks the number of subordinates
        when(mongoTemplate.find(queryForSubordinates, Mobster.class)).thenReturn(Collections.nCopies(50, aBoss));

        //For the boss
        when(mongoTemplate.find(queryForMobster, Mobster.class)).thenReturn(Collections.singletonList(aBoss));

        //THEN
        Mobster bossById = underTest.getBossById(AN_ID).block();
        assertOkMobster(aBoss, bossById);
    }

    @Test(expected = MobsterException.class)
    public void getAnExceptionBecauseTheBossIsNotABoss() {
        //GIVEN
        Mobster aBoss = getAMockedMobster(false);
        MobsterException expectedException = getNotABossMockedException(AN_ID);

        Query queryForSubordinates = new Query(Criteria.where(DIRECT_BOSS_FIELD).is(AN_ID));
        Query queryForMobster = new Query(Criteria.where(NICKNAME_FIELD).is(AN_ID));


        //WHEN
        //For the filter that checks the number of subordinates will fail because result is <50
        when(mongoTemplate.find(queryForSubordinates, Mobster.class)).thenReturn(Collections.nCopies(20, aBoss));

        //For the boss
        when(mongoTemplate.find(queryForMobster, Mobster.class)).thenReturn(Collections.singletonList(aBoss));


        //THEN
        Mono<Mobster> aResponse = underTest.getBossById(AN_ID);
        try {
            aResponse.block();
        } catch (MobsterException actualException) {
            assertException(expectedException, actualException);
            throw expectedException;
        }
    }

    @Test
    public void imprisonABoss() {

        //GIVEN
        Mobster aMobsterAboutToGoToJail = getAMockedMobster(false);
        Mobster theNewBoss = getAnotherMockedMobster();

        Query queryForSubordinates = new Query(Criteria.where(DIRECT_BOSS_FIELD).is(aMobsterAboutToGoToJail.getNickname()));
        Query queryForMobsterToImprison = new Query(Criteria.where(NICKNAME_FIELD).is(aMobsterAboutToGoToJail.getNickname()));
        Query queryForTheNewBoss = new Query(Criteria.where(NICKNAME_FIELD).is(theNewBoss.getNickname()));

        Query queryForTheOldest = new Query(Criteria.where(IN_PRISON_FIELD).is(false));
        queryForTheOldest.with(Sort.by(Sort.Direction.ASC, MOBSTER_SINCE_FIELD));
        queryForTheOldest.limit(1);


        //WHEN
        //When searching for the oldest mobster
        when(mongoTemplate.find(queryForTheOldest, Mobster.class))
                .thenReturn(Collections.singletonList(theNewBoss));

        //When searching for the new boss
        when(mongoTemplate.find(queryForTheNewBoss, Mobster.class))
                .thenReturn(Collections.singletonList(theNewBoss));

        //For the boss to imprison
        when(mongoTemplate.find(queryForMobsterToImprison, Mobster.class))
                .thenReturn(Collections.singletonList(aMobsterAboutToGoToJail));


        //THEN
        Mobster bossById = underTest.imprison(aMobsterAboutToGoToJail.getNickname()).block();
        assertOkMobster(theNewBoss, bossById);
    }

    @Test(expected = MobsterException.class)
    public void getAnExceptionWhenTryingToImprisonAnAlreadyMobsterInJail() {
        //GIVEN
        Mobster aBoss = getAMockedMobster(true);
        MobsterException expectedException = getAlreadyInJailMockedException(AN_ID);

        Query queryForMobster = new Query(Criteria.where(NICKNAME_FIELD).is(AN_ID));

        //WHEN
        when(mongoTemplate.find(queryForMobster, Mobster.class)).thenReturn(Collections.singletonList(aBoss));


        //THEN
        Mono<Mobster> aResponse = underTest.imprison(AN_ID);
        try {
            aResponse.block();
        } catch (MobsterException actualException) {
            assertException(expectedException, actualException);
            throw expectedException;
        }
    }

    @Test
    public void releaseABoss() {

        //GIVEN
        Mobster aMobsterAboutToSetFree = getAMockedMobster(true);

        //WHEN
        when(mongoTemplate.find(any(Query.class), eq(Mobster.class)))
                .thenReturn(Collections.singletonList(aMobsterAboutToSetFree));

        //THEN
        Mobster bossById = underTest.release(aMobsterAboutToSetFree.getNickname()).block();
        assertOkMobster(aMobsterAboutToSetFree, bossById);

    }


}
