package com.glassechidna.teamcity.awsrole;

import jetbrains.buildServer.serverSide.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AwsRoleFeature extends BuildFeature {
    static String FEATURE_TYPE = "awsrole";
    static String PARAMETER_NAME = "teamcityAwsRolePluginRoleArn";

    @NotNull
    @Override
    public String getType() {
        return FEATURE_TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Assume AWS IAM role";
    }

    @Nullable
    @Override
    public PropertiesProcessor getParametersProcessor() {
        return properties -> {
            ArrayList<InvalidProperty> invalid = new ArrayList<>();

//            String roleArn = properties.getOrDefault(PARAMETER_NAME, "");
//            if (!roleArn.matches("arn:aws:iam::\\d+:role/\\S+")) {
//                invalid.add(new InvalidProperty(PARAMETER_NAME, "Provided role '" + roleArn + "' doesn't match format arn:aws:iam::<account id>:role/<role name>"));
//            }

            return invalid;
        };
    }

    @Nullable
    @Override
    public Map<String, String> getDefaultParameters() {
        HashMap<String, String> map = new HashMap<>();
        map.put(PARAMETER_NAME, "some value");
        return map;
    }

    @NotNull
    @Override
    public String describeParameters(@NotNull Map<String, String> params) {
        return "This is my description";
    }

    @Nullable
    @Override
    public String getEditParametersUrl() {
        return "example.jsp";
    }
}
