package com.challenge.fbiapp.test;

import com.challenge.fbiapp.error.MobsterException;
import com.challenge.fbiapp.model.Mobster;
import com.challenge.fbiapp.resources.controllers.mob.BossesResource;
import com.challenge.fbiapp.services.db.MobsterDBService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

import static com.challenge.fbiapp.test.util.MobsterResourceTestHelper.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BossesResourceTest {

    private static final String AN_ID = "AN_ID";

    @Mock
    private MobsterDBService mobsterDBService;

    @InjectMocks
    private BossesResource underTest;

    @Test
    public void getABossTest() {

        //GIVEN
        Mobster aMockedMobster = getAMockedMobster();
        when(mobsterDBService.getBossById(anyString())).thenReturn(Mono.just(aMockedMobster));

        Mobster bossGotById = underTest.getBossById(AN_ID).block();

        assertMobster(aMockedMobster, bossGotById);

    }

    @Test(expected = MobsterException.class)
    public void getANonExistingMobsterException() {

        //GIVEN
        MobsterException expectedException = getAMobsterNotFoundException(AN_ID);
        when(mobsterDBService.getBossById(anyString())).thenThrow(expectedException);

        Mono<Mobster> aResponse = underTest.getBossById(AN_ID);

        try {
            aResponse.block();
        } catch (MobsterException actualException) {
            assertException(expectedException, actualException);
            throw expectedException;
        }

    }

    @Test(expected = MobsterException.class)
    public void getANotABossException() {

        //GIVEN
        MobsterException expectedException = getNotABossMockedException(AN_ID);
        when(mobsterDBService.getBossById(anyString())).thenThrow(expectedException);

        Mono<Mobster> aResponse = underTest.getBossById(AN_ID);

        try {
            aResponse.block();
        } catch (MobsterException actualException) {
            assertException(expectedException, actualException);
            throw expectedException;
        }

    }

}
