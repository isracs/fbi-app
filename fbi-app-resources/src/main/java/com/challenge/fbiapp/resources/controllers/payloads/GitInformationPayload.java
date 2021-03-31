package com.challenge.fbiapp.resources.controllers.payloads;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Component
@PropertySource("classpath:git.properties")
public class GitInformationPayload {
    @Value("${git.build.version}")
    private String buildVersion;

    @Value("${git.branch}")
    private String commitBranch;

    @Value("${git.commit.id}")
    private String commitId;

    @Value("${git.commit.message.full}")
    private String commitMessage;
}
