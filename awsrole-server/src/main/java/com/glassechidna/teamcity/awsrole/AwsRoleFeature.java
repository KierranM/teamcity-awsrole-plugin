package com.glassechidna.teamcity.awsrole;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.serverSide.BuildFeature;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.WebLinks;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.amazon.awssdk.services.sts.model.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AwsRoleFeature extends BuildFeature {
    private static Logger LOG = jetbrains.buildServer.log.Loggers.SERVER;

    private final String myEditUrl;
    private final String pluginVersion;
    private final String serverRoot;

    public AwsRoleFeature(@NotNull PluginDescriptor descriptor, WebLinks webLinks) {
        myEditUrl = descriptor.getPluginResourcesPath(AwsRoleConstants.EDIT_FEATURE_PAGE);
        pluginVersion = descriptor.getPluginVersion();
        serverRoot = webLinks.getRootUrl();
    }

    @NotNull
    @Override
    public String getType() {
        return AwsRoleConstants.FEATURE_TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Assume AWS IAM role";
    }

    @Nullable
    @Override
    public PropertiesProcessor getParametersProcessor() {
        return params -> {
            ArrayList<InvalidProperty> invalid = new ArrayList<>();

            if (AwsRoleUtil.getRoleArn(params) == "") {
                invalid.add(new InvalidProperty(AwsRoleConstants.ROLE_ARN_PARAMETER,  "AWS Role ARN is required"));
            }

            return invalid;
        };
    }

    @NotNull
    @Override
    public String describeParameters(@NotNull Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Role ARN: %s\n", AwsRoleUtil.getRoleArn(params)));
        builder.append(String.format("External Id: %s\n", AwsRoleUtil.getExternalId(params)));
        builder.append(String.format("Session Name: %s\n", AwsRoleUtil.getSessionName(params)));

        List<Tag> tags = AwsRoleUtil.getSessionTags(params);

        if (tags.size() > 0) {
            builder.append("Session Tags:\n");
            for (Tag tag : tags) {
                builder.append(String.format("%s => %s\n", tag.key(), tag.value()));
            }
        }

        return builder.toString();
    }

    @Nullable
    @Override
    public Map<String, String> getDefaultParameters() {
        Map<String, String> defaults = new HashMap<>();
        defaults.put(AwsRoleConstants.EXTERNAL_ID_PARAMETER, AwsRoleConstants.DEFAULT_EXTERNAL_ID);
        defaults.put(AwsRoleConstants.SESSION_NAME_PARAMETER, AwsRoleConstants.DEFAULT_SESSION_NAME);
        return defaults;
    }

    @Nullable
    @Override
    public String getEditParametersUrl() { return myEditUrl; }

    @Override
    public boolean isMultipleFeaturesPerBuildTypeAllowed() {
        return false;
    }
}
