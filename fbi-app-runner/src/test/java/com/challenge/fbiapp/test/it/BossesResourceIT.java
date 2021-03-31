package com.challenge.fbiapp.test.it;


import com.challenge.fbiapp.error.MobsterError;
import com.challenge.fbiapp.model.Mobster;
import com.challenge.fbiapp.test.it.helper.WiremockTestHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

import static com.challenge.fbiapp.test.it.helper.IntegrationAssertsHelper.assertMobster;
import static com.challenge.fbiapp.test.it.helper.IntegrationAssertsHelper.assertNotABossError;
import static com.challenge.fbiapp.test.it.helper.MongoDBForTesting.getCollectionToInsert;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConfiguration
@AutoConfigureDataMongo
public class BossesResourceIT {

    private static final String ENDPOINT_GET_BOSS = "/mafia/bosses/{bossId}";

    private TestRestTemplate restTemplate;

    @Autowired
    public void prepareDBForTests(MongoTemplate mongoTemplate) throws IOException {
        mongoTemplate.dropCollection("mobsters");
        List<Mobster> collectionToInsert = getCollectionToInsert();
        mongoTemplate.insert(collectionToInsert, "mobsters");
    }

    @LocalServerPort
    private int port;

    @BeforeClass
    public static void setupClass() {
        WiremockTestHelper.startServer();
    }

    @Before
    public void setup() {
        WiremockTestHelper.resetEndpoints();
        restTemplate = new TestRestTemplate();
    }

    @Test
    public void getAnExistingBossOK() {

        // GIVEN
        String bossNickname = "Fat";
        String bossName = "Tony";
        String bossSurname = "Soprano";
        String mobsterSince = "2021-01-15";

        String requestURL = WiremockTestHelper.getUrlToCallTheService(ENDPOINT_GET_BOSS, port, bossNickname);

        // WHEN
        ResponseEntity<Mobster> responseAsMobster = restTemplate.exchange(
                requestURL,
                HttpMethod.GET,
                new HttpEntity<Mobster>(null, new HttpHeaders()),
                Mobster.class);

        //THEN
        assertMobster(responseAsMobster, bossNickname, bossName, bossSurname, mobsterSince);

    }

    @Test
    public void gettingAMobsterButIsNotABoss() {

        String mobsterThatIsNotABoss = "Italy";
        String requestURL = WiremockTestHelper.getUrlToCallTheService(ENDPOINT_GET_BOSS, port, mobsterThatIsNotABoss);

        // WHEN
        ResponseEntity<MobsterError> notABossError = restTemplate.exchange(
                requestURL,
                HttpMethod.GET,
                new HttpEntity<Mobster>(null, new HttpHeaders()),
                MobsterError.class);


        //THEN
        assertNotABossError(notABossError);

    }


}
