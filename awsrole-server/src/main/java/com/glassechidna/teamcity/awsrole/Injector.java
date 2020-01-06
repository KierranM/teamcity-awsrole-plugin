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
        Collection<SBuildFeatureDescriptor> features = build.getBuildFeaturesOfType(AwsRoleFeature.FEATURE_TYPE);
        if (features.isEmpty()) {
            return;
        }

        Map<String, String> resolved = build.getValueResolver().resolve(buildParams);

        String roleArn = resolved.getOrDefault(AwsRoleFeature.PARAMETER_NAME, "");
        if (roleArn == "") {
            // fall back to checking legacy parameter name
            roleArn = resolved.getOrDefault("teamcityAwsRolePluginRoleArn", "");
            if (roleArn == "") {
                return;
            }
        }

        String buildTypeId = resolved.get("system.teamcity.buildType.id");
        String buildNumber = resolved.get("system.build.number");

        String externalId = resolved.getOrDefault(AwsRoleFeature.PARAMETER_PREFIX + "externalId", "");
        if (externalId == null || externalId.length() == 0) {
            externalId = buildTypeId;
        }

        String sessionName = resolved.getOrDefault(AwsRoleFeature.PARAMETER_PREFIX + "sessionName", "");
        if (sessionName == null || sessionName.length() == 0) {
            sessionName = String.format("%s_%s", buildTypeId, buildNumber);
        }

        int maximumSessionNameLength = 64;
        if (sessionName.length() > maximumSessionNameLength) {
            sessionName = sessionName.substring(sessionName.length() - maximumSessionNameLength);
        }

        List<Tag> tags = collectSessionTags(resolved);

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

    @NotNull
    private List<Tag> collectSessionTags(Map<String, String> resolved) {
        List<Tag> tags = new ArrayList<>();

        for (Map.Entry<String, String> entry : resolved.entrySet()) {
            if (entry.getKey().startsWith(AwsRoleFeature.PARAMETER_TAGS_PREFIX)) {
               String name = entry.getKey().replaceFirst(AwsRoleFeature.PARAMETER_TAGS_PREFIX, "");
               String value = entry.getValue();
               tags.add(Tag.builder().key(name).value(value).build());
            }
        }
        return tags;
    }

    private void putEnvironmentVariables(@NotNull Map<String, String> buildParams, Credentials c, List<Tag> tags) {
        buildParams.put("env.AWS_ACCESS_KEY_ID", c.accessKeyId());
        buildParams.put("env.AWS_SECRET_ACCESS_KEY", c.secretAccessKey());
        buildParams.put("env.AWS_SESSION_TOKEN", c.sessionToken());

        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> tagMap = new HashMap<>();
            for (Tag tag : tags) {
                tagMap.put(tag.key(), tag.value());
            }
            String tagJson = mapper.writeValueAsString(tagMap);
            buildParams.put("env.AWSROLE_TAGS", tagJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
