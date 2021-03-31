package com.challenge.fbiapp.test.it.helper;

import com.google.gson.Gson;
import com.challenge.fbiapp.model.Mobster;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.challenge.fbiapp.test.it.helper.WiremockTestHelper.getResponseFromClasspathJson;

public class MongoDBForTesting {


    private static final String MONGODB_FBI_RECORDS_JSON = "mongodb/fbi_records_it.json";

    public static List<Mobster> getCollectionToInsert() throws IOException {
        String recordsToSave = getResponseFromClasspathJson(MONGODB_FBI_RECORDS_JSON);

        Gson parser = new Gson();
        return Arrays.asList(parser.fromJson(recordsToSave, Mobster[].class));

    }

}
