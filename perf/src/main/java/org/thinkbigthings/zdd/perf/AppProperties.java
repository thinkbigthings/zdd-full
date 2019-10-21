package org.thinkbigthings.zdd.perf;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.time.Duration;

@ConfigurationProperties(prefix="connect")
public class AppProperties {

    protected String host;
    protected Integer port;
    protected boolean insertOnly = false;
    protected Duration latency = Duration.ofMillis(1);
    protected Integer threads = 2;
    protected Duration testDuration = Duration.ofMinutes(60);

    @ConstructorBinding
    public AppProperties(String host, Integer port, boolean insertOnly, Duration latency, Integer threads, Duration testDuration) {
        this.host = host;
        this.port = port;
        this.insertOnly = insertOnly;
        this.latency = latency;
        this.threads = threads;
        this.testDuration = testDuration;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public boolean isInsertOnly() {
        return insertOnly;
    }

    public Duration getLatency() {
        return latency;
    }

    public Integer getThreads() {
        return threads;
    }

    public Duration getTestDuration() {
        return testDuration;
    }
}