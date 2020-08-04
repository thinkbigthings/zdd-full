package org.thinkbigthings.zdd.server;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.time.Duration;

@ConstructorBinding
@ConfigurationProperties(prefix="app")
public class AppProperties {

    protected Integer apiVersion;

    public AppProperties(Integer apiVersion) {
        this.apiVersion = apiVersion;
    }

    public Integer getApiVersion() {
        return apiVersion;
    }

}