package com.omega.framework.task;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by jackychenb on 25/12/2016.
 */

@ConfigurationProperties("zookeeper")
public class ZooKeeperProperties {

    private String servers;
    private int sessionTimeout = 30000;

    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

}
