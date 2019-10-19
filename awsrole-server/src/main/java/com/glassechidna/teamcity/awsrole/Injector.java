package com.glassechidna.teamcity.awsrole;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.serverSide.ParametersPreprocessor;
import jetbrains.buildServer.serverSide.SRunningBuild;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;

import java.util.Map;

public class Injector implements ParametersPreprocessor {
    private static Logger LOG = jetbrains.buildServer.log.Loggers.SERVER;
    private StsClient client = StsClient.create();

    @Override
    public void fixRunBuildParameters(@NotNull SRunningBuild build, @NotNull Map<String, String> runParameters, @NotNull Map<String, String> buildParams) {
        String roleArn = buildParams.getOrDefault("aws.roleArn", "");
        if (roleArn == "") {
            return;
        }

        String buildTypeId = buildParams.get("system.teamcity.buildType.id");
        String buildNumber = buildParams.get("system.build.number");
        String sessionName = String.format("%s_%s", buildTypeId, buildNumber);

        int maximumSessionNameLength = 64;
        if (sessionName.length() > maximumSessionNameLength) {
            sessionName = sessionName.substring(sessionName.length() - maximumSessionNameLength);
        }

        AssumeRoleRequest request = AssumeRoleRequest.builder()
                .roleArn(roleArn)
                .roleSessionName(sessionName)
                .externalId(buildTypeId)
                .build();

        LOG.warn("Assuming AWS IAM role for build: " + request);
        AssumeRoleResponse assumeRoleResponse = client.assumeRole(request);

        Credentials c = assumeRoleResponse.credentials();
        buildParams.put("env.AWS_ACCESS_KEY_ID", c.accessKeyId());
        buildParams.put("env.AWS_SECRET_ACCESS_KEY", c.secretAccessKey());
        buildParams.put("env.AWS_SESSION_TOKEN", c.sessionToken());
    }
}