package com.challenge.fbiapp.test.it;


import com.challenge.fbiapp.model.Mobster;
import com.challenge.fbiapp.services.db.MobsterDBService;
import com.challenge.fbiapp.test.it.helper.WiremockTestHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

import static com.challenge.fbiapp.model.MobsterDBFields.DIRECT_BOSS_FIELD;
import static com.challenge.fbiapp.model.MobsterDBFields.FORMER_BOSSES_FIELD;
import static com.challenge.fbiapp.test.it.helper.IntegrationAssertsHelper.assertMobster;
import static com.challenge.fbiapp.test.it.helper.MongoDBForTesting.getCollectionToInsert;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConfiguration
@AutoConfigureDataMongo
public class CourtResourceIT {

    private static final String ENDPOINT_IMPRISON = "/mafia/court/{bossId}/imprison";
    private static final String ENDPOINT_RELEASE = "/mafia/court/{bossId}/release";

    private TestRestTemplate restTemplate;
    private MongoTemplate mongoTemplate;

    @LocalServerPort
    private int port;


    @Autowired
    public void prepareDBForTests(MongoTemplate mongoTemplate) throws IOException {
        mongoTemplate.dropCollection("mobsters");
        List<Mobster> collectionToInsert = getCollectionToInsert();
        mongoTemplate.insert(collectionToInsert, "mobsters");
        this.mongoTemplate = mongoTemplate;
    }


    @Autowired
    private MobsterDBService mobsterDBService;

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
    public void imprisonABoss() {

        // GIVEN
        String bossNicknameToImprison = "Fat";
        String requestURL = WiremockTestHelper.getUrlToCallTheService(ENDPOINT_IMPRISON, port, bossNicknameToImprison);

        // WHEN
        ResponseEntity<Mobster> theNewBoss = restTemplate.exchange(
                requestURL,
                HttpMethod.GET,
                new HttpEntity<Mobster>(null, new HttpHeaders()),
                Mobster.class);

        //THEN
        Assert.assertNotNull(theNewBoss);
        Assert.assertNotNull(theNewBoss.getBody());

        assertMobster(theNewBoss, "Italy", "Pauly", "Gualtieri", "2021-01-17");


        //Now we check that no one has Fat as boss
        Query query = new Query(
                Criteria.where(DIRECT_BOSS_FIELD)
                        .is(bossNicknameToImprison));

        List<Mobster> shouldBeEmpty = mongoTemplate.find(query, Mobster.class);

        Assert.assertNotNull(shouldBeEmpty);
        Assert.assertTrue(String.format("There's no any mobster with %s as a direct boss now", bossNicknameToImprison),
                shouldBeEmpty.isEmpty());

        //And now we check that someone has Fat as a former boss
        query = new Query(
                Criteria.where(FORMER_BOSSES_FIELD)
                        .is(bossNicknameToImprison));

        List<Mobster> thereWillBeManyMobstersWithAFormerBoss = mongoTemplate.find(query, Mobster.class);
        Assert.assertNotNull(thereWillBeManyMobstersWithAFormerBoss);
        Mobster aPossibleMobsterWithFormerBosses = thereWillBeManyMobstersWithAFormerBoss.stream().findFirst().orElse(null);

        Assert.assertNotNull("It has former Bosses", aPossibleMobsterWithFormerBosses);
        Assert.assertTrue("And the former bosses list contains the correct former boss",
                aPossibleMobsterWithFormerBosses.getFormerBosses().contains(bossNicknameToImprison));

    }


    @Test
    public void releaseABoss() {

        // GIVEN
        String bossNicknameToRelease = "Fat";

        //Check if the mobster is in prison. If it's not, let's imprison him for release afterwards
        imprisonAMobsterForTesting(bossNicknameToRelease);


        String requestURL = WiremockTestHelper.getUrlToCallTheService(ENDPOINT_RELEASE, port, bossNicknameToRelease);

        // WHEN
        ResponseEntity<Mobster> releasedBoss = restTemplate.exchange(
                requestURL,
                HttpMethod.GET,
                new HttpEntity<Mobster>(null, new HttpHeaders()),
                Mobster.class);

        //THEN
        Assert.assertNotNull(releasedBoss);
        Assert.assertNotNull(releasedBoss.getBody());
        Assert.assertFalse(releasedBoss.getBody().isInPrison());

        assertMobster(releasedBoss, "Fat", "Tony", "Soprano", "2021-01-15");


        //Now we check that no one has Fat as boss
        Query query = new Query(
                Criteria.where(FORMER_BOSSES_FIELD)
                        .is(bossNicknameToRelease));

        List<Mobster> shouldBeEmpty = mongoTemplate.find(query, Mobster.class);

        Assert.assertNotNull(shouldBeEmpty);
        Assert.assertTrue(String.format("There's no any mobster with %s as a former boss now", bossNicknameToRelease),
                shouldBeEmpty.isEmpty());

        //And now we check that someone has Fat as a former boss
        query = new Query(
                Criteria.where(DIRECT_BOSS_FIELD)
                        .is(bossNicknameToRelease));

        List<Mobster> thereWillBeManyWithThisDirectBoss = mongoTemplate.find(query, Mobster.class);
        Assert.assertNotNull(thereWillBeManyWithThisDirectBoss);
        Mobster aPossibleMobsterWithThisDirectBoss = thereWillBeManyWithThisDirectBoss.stream().findFirst().orElse(null);

        Assert.assertNotNull("Someone has that boss", aPossibleMobsterWithThisDirectBoss);
        Assert.assertEquals("Double check!", aPossibleMobsterWithThisDirectBoss.getDirectBoss(), bossNicknameToRelease);

    }

    private void imprisonAMobsterForTesting(String bossNicknameToRelease) {
        mobsterDBService.getBossById(bossNicknameToRelease).map(bossFound -> {
                    if (!bossFound.isInPrison()) {
                        log.info("Let's imprison {} to release him afterwards", bossNicknameToRelease);
                        return mobsterDBService.imprison(bossNicknameToRelease).map(theNewBoss -> theNewBoss);
                    } else {
                        log.info("So {} is in Jail, so will release him", bossNicknameToRelease);
                        return bossFound;
                    }
                }
        ).subscribe(value -> Assert.assertNotNull(value),
                error -> Assert.assertNull(error));
    }

}
