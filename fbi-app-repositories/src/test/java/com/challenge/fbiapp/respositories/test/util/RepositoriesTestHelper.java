package com.challenge.fbiapp.respositories.test.util;

import com.challenge.fbiapp.error.MobsterError;
import com.challenge.fbiapp.error.MobsterErrorCodes;
import com.challenge.fbiapp.error.MobsterException;
import com.challenge.fbiapp.model.Mobster;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RepositoriesTestHelper {

    public static Mobster getAMockedMobster(boolean inPrison) {
        return Mobster.builder()
                .nickname("Fat")
                .name("Tony")
                .surName("Soprano")
                .inPrison(inPrison)
                .since("2021-01-15")
                .build();
    }

    public static Mobster getAnotherMockedMobster() {
        return Mobster.builder()
                .nickname("Italy")
                .name("Pauly")
                .surName("Gualtieri")
                .inPrison(false)
                .since("2021-01-17")
                .directBoss("Fat")
                .build();
    }

    public static void assertOkMobster(Mobster expected, Mobster actual) {
        assertNotNull(actual);
        assertEquals(expected.getNickname(), actual.getNickname());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getSurName(), actual.getSurName());
        assertEquals(expected.getSince(), actual.getSince());
        assertEquals(expected.isInPrison(), actual.isInPrison());
        assertEquals(expected.getDirectBoss(), actual.getDirectBoss());
    }

    public static MobsterException getAMobsterNotFoundExceptionWhenExecuteFind(String mobsterId) {
        return MobsterException.builder().httpStatus(HttpStatus.NOT_FOUND)
                .error(MobsterError.builder()
                        .code(MobsterErrorCodes.NOT_FOUND)
                        .message(String.format("The Mobster with nickname '%s' was not found", mobsterId))
                        .build())
                .build();
    }

    public static MobsterException getNotABossMockedException(String mobsterId) {
        return MobsterException.builder().httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .error(MobsterError.builder()
                        .code(MobsterErrorCodes.NOT_A_BOSS)
                        .message(String.format("The Mobster %s is not a boss because it does not have enough subordinates",
                                mobsterId))
                        .build())
                .build();
    }

    public static MobsterException getAlreadyInJailMockedException(String mobsterId) {
        return MobsterException.builder().httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .error(MobsterError.builder()
                        .code(MobsterErrorCodes.ALREADY_IN_JAIL)
                        .message(String.format("The mobster %s is already in Jail", mobsterId))
                        .build())
                .build();
    }

    public static void assertException(MobsterException expected, MobsterException actual) {
        assertEquals(expected.getHttpStatus(), actual.getHttpStatus());
        assertEquals(expected.getError().getCode(), actual.getError().getCode());
        assertEquals(expected.getError().getMessage(), actual.getError().getMessage());
    }

}
