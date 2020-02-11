package com.glassechidna.teamcity.awsrole;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;
import software.amazon.awssdk.services.sts.model.Tag;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class Injector implements ParametersPreprocessor {
    private static Logger LOG = jetbrains.buildServer.log.Loggers.SERVER;
    private StsClient client;

    public Injector(PluginDescriptor descriptor, WebLinks webLinks) {
        String userAgentSuffix = "plugin/" + descriptor.getPluginVersion();

        try {
            URI root = new URI(webLinks.getRootUrl());
            userAgentSuffix += " server/" + root.getHost();
        } catch (URISyntaxException e) {
            // no-op
        }

        ClientOverrideConfiguration coc = ClientOverrideConfiguration
                .builder()
                .putAdvancedOption(SdkAdvancedClientOption.USER_AGENT_SUFFIX, userAgentSuffix)
                .build();
        this.client = StsClient.builder().overrideConfiguration(coc).build();
    }

    private void logMap(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            LOG.warn(entry.getKey() + " = " + entry.getValue());
        }
    }

    @Override
    public void fixRunBuildParameters(@NotNull SRunningBuild build, @NotNull Map<String, String> runParameters, @NotNull Map<String, String> buildParams) {
        Collection<SBuildFeatureDescriptor> features = build.getBuildFeaturesOfType(AwsRoleConstants.FEATURE_TYPE);
        if (features.isEmpty()) {
            return;
        }

        Map<String, String> resolved = build.getValueResolver().resolve(buildParams);

        String roleArn = AwsRoleUtil.getRoleArn(resolved);
        String externalId = AwsRoleUtil.getExternalId(resolved);
        String sessionName = AwsRoleUtil.getSessionName(resolved);

        List<Tag> tags = AwsRoleUtil.getSessionTags(resolved);

        AssumeRoleRequest request = AssumeRoleRequest.builder()
                .roleArn(roleArn)
                .roleSessionName(sessionName)
                .externalId(externalId)
                .tags(tags)
                .build();

        LOG.warn("Assuming AWS IAM role for build: " + request);
        AssumeRoleResponse assumeRoleResponse = client.assumeRole(request);
        putEnvironmentVariables(buildParams, assumeRoleResponse.credentials(), tags);
    }

    private void putEnvironmentVariables(@NotNull Map<String, String> buildParams, Credentials c, List<Tag> tags) {
        buildParams.put(AwsRoleConstants.AGENT_ACCESS_KEY_ID_PARAMETER, c.accessKeyId());
        buildParams.put(AwsRoleConstants.AGENT_SECRET_ACCESS_KEY_PARAMETER, c.secretAccessKey());
        buildParams.put(AwsRoleConstants.AGENT_SESSION_TOKEN_PARAMETER, c.sessionToken());

        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> tagMap = new HashMap<>();
            for (Tag tag : tags) {
                tagMap.put(tag.key(), tag.value());
            }
            String tagJson = mapper.writeValueAsString(tagMap);
            buildParams.put(AwsRoleConstants.AGENT_ROLE_TAGS_PARAMETER, tagJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
