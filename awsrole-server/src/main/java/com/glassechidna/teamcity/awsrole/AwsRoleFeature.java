package com.glassechidna.teamcity.awsrole;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.serverSide.BuildFeature;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.WebLinks;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

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

    @Nullable
    @Override
    public String getEditParametersUrl() { return myEditUrl; }
}
