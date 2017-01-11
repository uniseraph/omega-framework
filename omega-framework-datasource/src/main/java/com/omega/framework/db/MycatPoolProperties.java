package com.omega.framework.db;

import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 * Created by jackychenb on 26/12/2016.
 */
public class MycatPoolProperties extends PoolProperties {

    private volatile MycatLocator mycatLocator;

    public MycatLocator getMycatLocator() {
        return mycatLocator;
    }

    public void setMycatLocator(MycatLocator mycatLocator) {
        this.mycatLocator = mycatLocator;
    }

}
