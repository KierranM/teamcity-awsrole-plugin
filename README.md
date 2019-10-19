# `awsrole` TeamCity plugin

This is a plugin for TeamCity (2017.1 and newer) to enable more secure operation
in AWS.

## The problem

Typically, build agents either have an instance profile associated with them (if
running on an EC2 instance) or an IAM access key (if running on-premises). The
problem with these approaches is twofold:

Firstly, logs in CloudTrail are not as useful as they could be. It is almost
impossible to correlate AWS API calls with the TeamCity build that initiated them.

Secondly, it's difficult to define granular IAM permissions on a per-project or
per-build configuration basis. This usually results in either a lot of manual
effort or granting permissions that are too powerful.

## The solution

The TeamCity **server** should have an IAM role that has `sts:AssumeRole` permissions
to assume roles needed by build steps. Build configurations can define an `aws.roleArn`
configuration parameter, e.g. `arn:aws:iam::1234567890:role/ApiDeployer`. 

Whenever  a build with this configuration parameter is started, the **server** 
assumes the requested role and sets `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`
and `AWS_SESSION_TOKEN` environment variables. 

Crucially, it sets the role session name to `%system.teamcity.buildType.id%_%system.build.number%` 
and the [external id][external-id] to `%system.teamcity.buildType.id%`. The former
means that CloudTrail logs will identify the build making API calls and the latter
allows roles to be locked down to only allow specific projects to assume them.

## Download

The compiled plugin ZIP can be downloaded from the [Releases][releases] tab.

[external-id]: https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create_for-user_externalid.html
[releases]: https://github.com/glassechidna/teamcity-awsrole-plugin/releases
