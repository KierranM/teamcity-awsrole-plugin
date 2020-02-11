package com.glassechidna.teamcity.awsrole;

public class AwsRoleConstants {
    public static final String FEATURE_TYPE = "awsrole";
    public static final String EDIT_FEATURE_PAGE = "awsRoleFeature.jsp";

    public static final String PARAMETER_PREFIX = "awsrole.";
    public static final String ROLE_ARN_PARAMETER = PARAMETER_PREFIX + "roleArn";
    public static final String LEGACY_ROLE_ARN_PARAMETER = "teamcityAwsRolePluginRoleArn";
    public static final String SESSION_TAGS_PARAMETER = PARAMETER_PREFIX + "sessionTags";
    public static final String EXTERNAL_ID_PARAMETER = PARAMETER_PREFIX + "externalId";
    public static final String SESSION_NAME_PARAMETER = PARAMETER_PREFIX + "sessionName";

    public static final String AGENT_ACCESS_KEY_ID_PARAMETER = "env.AWS_ACCESS_KEY_ID";
    public static final String AGENT_SECRET_ACCESS_KEY_PARAMETER = "env.AWS_SECRET_ACCESS_KEY";
    public static final String AGENT_SESSION_TOKEN_PARAMETER = "env.AWS_SESSION_TOKEN";
    public static final String AGENT_ROLE_TAGS_PARAMETER = "env.AWSROLE_TAGS";

    public static final Integer MAXIMUM_ROLE_SESSION_NAME_LENGTH = 64;
}
