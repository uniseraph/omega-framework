package com.omega.framework.db;

import org.apache.tomcat.jdbc.pool.PoolConfiguration;

import java.sql.SQLException;

/**
 * Created by jackychenb on 24/12/2016.
 */
public class MycatDataSourceProxy extends org.apache.tomcat.jdbc.pool.DataSourceProxy {

    public MycatDataSourceProxy() {

    }

    public MycatDataSourceProxy(PoolConfiguration poolProperties) {
        super(poolProperties);
    }

    public org.apache.tomcat.jdbc.pool.ConnectionPool createPool() throws SQLException {
        if (pool != null) {
            return pool;
        } else {
            return pCreatePool();
        }
    }

    private synchronized org.apache.tomcat.jdbc.pool.ConnectionPool pCreatePool() throws SQLException {
        if (pool != null) {
            return pool;
        } else {
            pool = new MycatConnectionPool(poolProperties);
            return pool;
        }
    }

}
