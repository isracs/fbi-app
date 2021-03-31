package com.challenge.fbiapp.test.it;


import com.challenge.fbiapp.test.it.helper.IntegrationAssertsHelper;
import com.challenge.fbiapp.error.MobsterError;
import com.challenge.fbiapp.model.Mobster;
import com.challenge.fbiapp.test.it.helper.WiremockTestHelper;
import org.junit.*;
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

import static com.challenge.fbiapp.test.it.helper.MongoDBForTesting.getCollectionToInsert;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConfiguration
@AutoConfigureDataMongo
public class MobsterResourceIT {

    private static final String ENDPOINT_GET_MOBSTER = "/mafia/mobster/{mobsterId}";

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
    public void getMobsterWithFormerBosses() {

        // GIVEN
        String bossNickname = "Don";
        String bossName = "Vito";
        String bossSurname = "Corleone";
        String mobsterSince = "2021-01-21";
        String requestURL = WiremockTestHelper.getUrlToCallTheService(ENDPOINT_GET_MOBSTER, port, "Don");


        // WHEN
        ResponseEntity<Mobster> responseAsMobster = restTemplate.exchange(
                requestURL,
                HttpMethod.GET,
                new HttpEntity<Mobster>(null, new HttpHeaders()),
                Mobster.class);

        //THEN
        IntegrationAssertsHelper.assertMobster(responseAsMobster, bossNickname, bossName, bossSurname, mobsterSince);
        Assert.assertNotNull("He has former bosses array", responseAsMobster.getBody().getFormerBosses());
    }

    @Test
    public void getMobsterWithNoDirectBossBecauseHeIsTHEBoss() {
        // GIVEN
        String bossNickname = "Fat";
        String bossName = "Tony";
        String bossSurname = "Soprano";
        String mobsterSince = "2021-01-15";

        String requestURL = WiremockTestHelper.getUrlToCallTheService(ENDPOINT_GET_MOBSTER, port, bossNickname);


        // WHEN
        ResponseEntity<Mobster> responseAsMobster = restTemplate.exchange(
                requestURL,
                HttpMethod.GET,
                new HttpEntity<Mobster>(null, new HttpHeaders()),
                Mobster.class);

        //THEN
        IntegrationAssertsHelper.assertMobster(
                responseAsMobster, bossNickname, bossName, bossSurname, mobsterSince);
        Assert.assertNull("He has no boss", responseAsMobster.getBody().getDirectBoss());

    }

    @Test
    public void getAnExistingMobsterOK() {

        // GIVEN
        String mobsterNickname = "Italy";
        String mobsterName = "Pauly";
        String mobsterSurname = "Gualtieri";
        String mobsterSince = "2021-01-17";
        String directBoss = "Fat";

        String requestURL = WiremockTestHelper.getUrlToCallTheService(ENDPOINT_GET_MOBSTER, port, mobsterNickname);


        // WHEN
        ResponseEntity<Mobster> responseAsMobster = restTemplate.exchange(
                requestURL,
                HttpMethod.GET,
                new HttpEntity<Mobster>(null, new HttpHeaders()),
                Mobster.class);

        //THEN
        IntegrationAssertsHelper.assertMobsterWithBoss(
                responseAsMobster, mobsterNickname, directBoss, mobsterName, mobsterSurname, mobsterSince);

    }

    @Test
    public void getAnNonExistingMobster() {

        String fakeMobsterNickname = "Invent";
        String requestURL = WiremockTestHelper.getUrlToCallTheService(ENDPOINT_GET_MOBSTER, port, fakeMobsterNickname);

        // WHEN
        ResponseEntity<MobsterError> mobsterNotFound = restTemplate.exchange(
                requestURL,
                HttpMethod.GET,
                new HttpEntity<Mobster>(null, new HttpHeaders()),
                MobsterError.class);

        //THEN
        IntegrationAssertsHelper.assertMobsterNotFound(mobsterNotFound);

    }


}
