package com.challenge.fbiapp.test.services;

import com.challenge.fbiapp.dao.MobstersDBDAO;
import com.challenge.fbiapp.model.Mobster;
import com.challenge.fbiapp.services.db.impl.MobsterDBServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MobsterDBServiceTest {

    private static String AN_ID = "AN_ID";

    @Mock
    private MobstersDBDAO mobstersDBDAO;

    @InjectMocks
    private MobsterDBServiceImpl underTest;

    @Test
    public void gotAMobsterByIDByService() {
        //GIVEN
        Mobster aMockedMobster = getAMockedMobster();
        when(mobstersDBDAO.getMobsterById(anyString())).thenReturn(Mono.just(aMockedMobster));
        //WHEN
        Mobster bossById = underTest.getMobsterById(AN_ID).block();
        //THEN
        assertOkMobster(aMockedMobster, bossById);
    }

    @Test
    public void gotABossByIDByService() {
        //GIVEN
        Mobster aMockedMobster = getAMockedMobster();
        when(mobstersDBDAO.getBossById(anyString())).thenReturn(Mono.just(aMockedMobster));
        //WHEN
        Mobster bossById = underTest.getBossById(AN_ID).block();
        //THEN
        assertOkMobster(aMockedMobster, bossById);
    }

    @Test
    public void imprisonTestByService() {
        //GIVEN
        Mobster aMockedMobster = getAMockedMobster();
        when(mobstersDBDAO.imprison(anyString())).thenReturn(Mono.just(aMockedMobster));
        //WHEN
        Mobster bossById = underTest.imprison(AN_ID).block();
        //THEN
        assertOkMobster(aMockedMobster, bossById);
    }

    @Test
    public void releaseByService() {
        //GIVEN
        Mobster aMockedMobster = getAMockedMobster();
        when(mobstersDBDAO.release(anyString())).thenReturn(Mono.just(aMockedMobster));
        //WHEN
        Mobster bossById = underTest.release(AN_ID).block();
        //THEN
        assertOkMobster(aMockedMobster, bossById);
    }

    private Mobster getAMockedMobster() {
        return Mobster.builder()
                .nickname("Fat")
                .name("Tony")
                .surName("Soprano")
                .inPrison(false)
                .since("2021-01-15")
                .build();
    }


    private void assertOkMobster(Mobster expected, Mobster actual) {
        assertNotNull(actual);
        assertEquals(expected.getNickname(), actual.getNickname());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getSurName(), actual.getSurName());
        assertEquals(expected.getSince(), actual.getSince());
        assertEquals(expected.isInPrison(), actual.isInPrison());
        assertEquals(expected.getDirectBoss(), actual.getDirectBoss());
    }

}
