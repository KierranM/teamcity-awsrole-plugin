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

        if (params.containsKey("env.AWS_SECRET_ACCESS_KEY")) {
            String sak = params.get("env.AWS_SECRET_ACCESS_KEY");
            runningBuild.getPasswordReplacer().addPassword(sak);

            String tok = params.get("env.AWS_SESSION_TOKEN");
            runningBuild.getPasswordReplacer().addPassword(tok);
        }
    }
}
