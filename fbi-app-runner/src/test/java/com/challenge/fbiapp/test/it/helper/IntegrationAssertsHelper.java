package com.challenge.fbiapp.test.it.helper;

import com.challenge.fbiapp.error.MobsterError;
import com.challenge.fbiapp.model.Mobster;
import org.junit.Assert;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class IntegrationAssertsHelper {

    public static void assertMobster(ResponseEntity<Mobster> responseAsMobster, String nickname, String name, String surname, String since) {
        Assert.assertNotNull(responseAsMobster);
        Assert.assertNotNull(responseAsMobster.getBody());
        Assert.assertEquals("The nickname is the id of the request", nickname, responseAsMobster.getBody().getNickname());
        Assert.assertEquals("... his name is", name, responseAsMobster.getBody().getName());
        Assert.assertEquals("... his surname is", surname, responseAsMobster.getBody().getSurName());
        Assert.assertEquals("... he is a mobster since", since, responseAsMobster.getBody().getSince());
    }

    public static void assertMobsterWithBoss(ResponseEntity<Mobster> responseAsMobster, String nickname, String boss, String name, String surname, String since) {
        assertMobster(responseAsMobster, nickname, name, surname, since);
        Assert.assertEquals("... his boss is", boss, responseAsMobster.getBody().getDirectBoss());
    }

    public static void assertMobsterNotFound(ResponseEntity<MobsterError> mobsterNotFound) {
        Assert.assertNotNull(mobsterNotFound);
        Assert.assertNotNull(mobsterNotFound.getBody());
        Assert.assertEquals("The http code is '404'", HttpStatus.NOT_FOUND, mobsterNotFound.getStatusCode());
        Assert.assertEquals("The code is 'not found'", "NOT_FOUND", mobsterNotFound.getBody().getCode());
        Assert.assertTrue("The message is tells what nickname was not found",
                mobsterNotFound.getBody().getMessage().matches("The Mobster with nickname (.*?) was not found"));
    }

    public static void assertNotABossError(ResponseEntity<MobsterError> notABossError) {
        Assert.assertNotNull(notABossError);
        Assert.assertNotNull(notABossError.getBody());
        Assert.assertEquals("The http code is '500'", HttpStatus.INTERNAL_SERVER_ERROR, notABossError.getStatusCode());
        Assert.assertEquals("The code is 'not a boss'", "NOT_A_BOSS", notABossError.getBody().getCode());
        Assert.assertTrue("The error message has the correct pattern",
                notABossError.getBody().getMessage().matches("The Mobster (.*?) is not a boss because it does not have enough subordinates"));
    }

}
