package com.challenge.fbiapp.test;

import com.challenge.fbiapp.error.MobsterException;
import com.challenge.fbiapp.model.Mobster;
import com.challenge.fbiapp.resources.controllers.mob.CourtResource;
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
public class CourtResourceTest {

    private static final String AN_ID = "AN_ID";

    @Mock
    private MobsterDBService mobsterDBService;

    @InjectMocks
    private CourtResource underTest;

    @Test
    public void imprisonTest() {

        //GIVEN
        Mobster aMockedMobster = getAMockedMobster();
        when(mobsterDBService.imprison(anyString())).thenReturn(Mono.just(aMockedMobster));

        Mobster bossById = underTest.imprison(AN_ID).block();

        assertMobster(aMockedMobster, bossById);

    }

    @Test(expected = MobsterException.class)
    public void getAnAlreadyIJailException() {

        //GIVEN
        MobsterException expectedException = getAlreadyInJailMockedException(AN_ID);
        when(mobsterDBService.imprison(anyString())).thenThrow(expectedException);

        Mono<Mobster> aResponse = underTest.imprison(AN_ID);

        try {
            aResponse.block();
        } catch (MobsterException actualException) {
            assertException(expectedException, actualException);
            throw expectedException;
        }

    }

    @Test
    public void releaseTest() {

        //GIVEN
        Mobster aMockedMobster = getAMockedMobster();
        when(mobsterDBService.release(anyString())).thenReturn(Mono.just(aMockedMobster));

        Mobster bossById = underTest.release(AN_ID).block();

        assertMobster(aMockedMobster, bossById);

    }


    @Test(expected = MobsterException.class)
    public void getANonExistingMobsterExceptionWhenImprison() {

        //GIVEN
        MobsterException expectedException = getAMobsterNotFoundException(AN_ID);
        when(mobsterDBService.imprison(anyString())).thenThrow(expectedException);

        Mono<Mobster> aResponse = underTest.imprison(AN_ID);

        try {
            aResponse.block();
        } catch (MobsterException actualException) {
            assertException(expectedException, actualException);
            throw expectedException;
        }

    }

    @Test(expected = MobsterException.class)
    public void getANonExistingMobsterExceptionWhenReleasing() {

        //GIVEN
        MobsterException expectedException = getAMobsterNotFoundException(AN_ID);
        when(mobsterDBService.release(anyString())).thenThrow(expectedException);

        Mono<Mobster> aResponse = underTest.release(AN_ID);

        try {
            aResponse.block();
        } catch (MobsterException actualException) {
            assertException(expectedException, actualException);
            throw expectedException;
        }

    }

}
