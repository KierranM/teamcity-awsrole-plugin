package com.glassechidna.teamcity.awsrole;

public class AwsRoleParametersProvider {
    public String getIamAccessKeyId() {
        return AwsRoleConstants.ACCESS_KEY_ID_PARAMETER;
    }

    public String getIamSecretKey() {
        return AwsRoleConstants.SECRET_ACCESS_KEY_PARAMETER;
    }

    public String getRoleArn() {
        return AwsRoleConstants.ROLE_ARN_PARAMETER;
    }

    public String getSessionTags() {
        return AwsRoleConstants.SESSION_TAGS_PARAMETER;
    }

    public String getExternalId() {
        return AwsRoleConstants.EXTERNAL_ID_PARAMETER;
    }

    public String getSessionName() {
        return AwsRoleConstants.SESSION_NAME_PARAMETER;
    }

    public String getSessionDuration() {
        return AwsRoleConstants.SESSION_DURATION_PARAMETER;
    }

}
