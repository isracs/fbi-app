package com.challenge.fbiapp.test.it.helper;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;


@Slf4j
public class WiremockTestHelper {

    private static WireMockServer wireMockServer;

    private static final ResponseDefinitionBuilder NOT_FOUND = WireMock.aResponse()
            .withStatus(HttpStatus.NOT_FOUND.value());

    public static synchronized void startServer() {
        if (wireMockServer == null) {
            final WireMockConfiguration wireMockConfiguration = WireMockConfiguration.wireMockConfig()
                    .dynamicPort();
            wireMockServer = new WireMockServer(wireMockConfiguration);
            wireMockServer.start();

        }
    }

    public static void resetEndpoints() {
        wireMockServer.resetMappings();
        wireMockServer.givenThat(WireMock.get(UrlPattern.ANY).willReturn(NOT_FOUND).atPriority(100));
        wireMockServer.givenThat(WireMock.post(UrlPattern.ANY).willReturn(NOT_FOUND).atPriority(100));
        wireMockServer.givenThat(WireMock.put(UrlPattern.ANY).willReturn(NOT_FOUND).atPriority(100));
        wireMockServer.givenThat(WireMock.delete(UrlPattern.ANY).willReturn(NOT_FOUND).atPriority(100));
    }

    public static String getUrlToCallTheService(String path, int port, String mobsterNickname) {
        return UriComponentsBuilder.newInstance()
                .scheme("http").host("localhost").port(port)
                .path(path)
                .buildAndExpand(mobsterNickname)
                .toUriString().trim();
    }


    static String getResponseFromClasspathJson(String thisResponse) throws IOException {
        ClassLoader classLoader = WiremockTestHelper.class.getClassLoader();
        return FileUtils.readFileToString(new File(classLoader.getResource(thisResponse).getFile()), "UTF-8");
    }

}
