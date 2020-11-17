package org.thinkbigthings.zdd.perf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.time.Duration;

@ConstructorBinding
@ConfigurationProperties(prefix="connect")
public class AppProperties {

    protected String host;
    protected boolean insertOnly = false;
    protected Integer threads = 2;
    protected Duration testDuration = Duration.ofMinutes(60);

    public AppProperties(String host, boolean insertOnly, Integer threads, Duration testDuration) {
        this.host = host;
        this.insertOnly = insertOnly;
        this.threads = threads;
        this.testDuration = testDuration;
    }

    public String getHost() {
        return host;
    }

    public boolean isInsertOnly() {
        return insertOnly;
    }

    public Integer getThreads() {
        return threads;
    }

    public Duration getTestDuration() {
        return testDuration;
    }
}