package com.challenge.fbiapp.test;

import com.challenge.fbiapp.error.MobsterException;
import com.challenge.fbiapp.model.Mobster;
import com.challenge.fbiapp.resources.controllers.mob.MobsterResource;
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
public class MobsterResourceTest {

    private static final String AN_ID = "AN_ID";

    @Mock
    private MobsterDBService mobsterDBService;

    @InjectMocks
    private MobsterResource underTest;

    @Test
    public void getAMobsterTest() {

        //GIVEN
        Mobster aMockedMobster = getAMockedMobster();
        when(mobsterDBService.getMobsterById(anyString())).thenReturn(Mono.just(aMockedMobster));

        Mobster bossById = underTest.getMobsterById(AN_ID).block();

        assertMobster(aMockedMobster, bossById);

    }


    @Test(expected = MobsterException.class)
    public void getANonExistingMobsterException() {

        //GIVEN
        MobsterException expectedException = getAMobsterNotFoundException(AN_ID);
        when(mobsterDBService.getMobsterById(anyString())).thenThrow(expectedException);

        Mono<Mobster> aResponse = underTest.getMobsterById(AN_ID);

        try {
            aResponse.block();
        } catch (MobsterException actualException) {
            assertException(expectedException, actualException);
            throw expectedException;
        }

    }

}
