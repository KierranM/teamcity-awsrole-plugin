package com.glassechidna.teamcity.awsrole;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.AgentLifeCycleAdapter;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Masker extends AgentLifeCycleAdapter {
    private static Logger LOG = Loggers.AGENT;

    public Masker(EventDispatcher<AgentLifeCycleListener> dispatcher) {
        dispatcher.addListener(this);
        LOG.warn("awsrole plugin registered");
    }

    @Override
    public void buildStarted(@NotNull AgentRunningBuild runningBuild) {
        Map<String, String> params = runningBuild.getSharedBuildParameters().getAllParameters();

        if (params.containsKey(AwsRoleConstants.AGENT_SECRET_ACCESS_KEY_PARAMETER)) {
            String sak = params.get(AwsRoleConstants.AGENT_SECRET_ACCESS_KEY_PARAMETER);
            runningBuild.getPasswordReplacer().addPassword(sak);

            String tok = params.get(AwsRoleConstants.AGENT_SESSION_TOKEN_PARAMETER);
            runningBuild.getPasswordReplacer().addPassword(tok);
        }
    }
}
