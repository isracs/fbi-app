package com.challenge.fbiapp.test;

import com.challenge.fbiapp.resources.controllers.health.GitInformationResource;
import com.challenge.fbiapp.resources.controllers.payloads.GitInformationPayload;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

@RunWith(MockitoJUnitRunner.class)
public class GitInformationResourceTest {

    @Mock
    private GitInformationPayload gitInformation;

    @InjectMocks
    private GitInformationResource gitInformationController;

    @Before
    public void beforeTests() {
        Mockito.when(gitInformation.getCommitBranch()).thenReturn("branch");
        Mockito.when(gitInformation.getBuildVersion()).thenReturn("buildVersion");
        Mockito.when(gitInformation.getCommitId()).thenReturn("commitId");
        Mockito.when(gitInformation.getCommitMessage()).thenReturn("commitMessageFull");
    }

    @Test
    public void shouldReturnGitInformation() {
        // WHEN
        Mono<GitInformationPayload> response = this.gitInformationController.getGitInformation();

        // THEN
        GitInformationPayload gitInformationPayload = response.block();
        Assert.assertNotNull(gitInformationPayload);
        Assert.assertEquals(gitInformation.getCommitBranch(), gitInformationPayload.getCommitBranch());
        Assert.assertEquals(gitInformation.getBuildVersion(), gitInformationPayload.getBuildVersion());
        Assert.assertEquals(gitInformation.getCommitId(), gitInformationPayload.getCommitId());
        Assert.assertEquals(gitInformation.getCommitMessage(), gitInformationPayload.getCommitMessage());
    }

}
