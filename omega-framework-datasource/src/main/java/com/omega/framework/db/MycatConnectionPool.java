package com.omega.framework.db;

import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.client.ServiceInstance;

import java.sql.SQLException;

/**
 * Created by jackychenb on 24/12/2016.
 */
public class MycatConnectionPool extends org.apache.tomcat.jdbc.pool.ConnectionPool {

    private static final Logger logger = LoggerFactory.getLogger(MycatConnectionPool.class);

    public MycatConnectionPool(PoolConfiguration prop) throws SQLException {
        super(prop);
    }

    @Override
    protected PooledConnection create(boolean incrementCounter) {
        // 注意：因为无法访问到父类的size成员变量，所以通过create方法简介增加计数，但直接丢弃返回值
        super.create(incrementCounter);

        PoolProperties cfg = new PoolProperties();
        BeanUtils.copyProperties(getPoolProperties(), cfg);

        String url = cfg.getUrl();
        int start = url.indexOf("//") + 2;
        int end = url.indexOf('/', start);
        String server = url.substring(start, end);

        try {
            MycatLocator mycatLocator = ((MycatPoolProperties) getPoolProperties()).getMycatLocator();
            ServiceInstance instance = mycatLocator.getServiceInstance();
            if (instance == null) {
                throw new Exception("Cannot connect to " + server);
            }

            String realUrl = url.substring(0, start) + instance.getHost() + ":" + instance.getPort() + url.substring(end);
            cfg.setUrl(realUrl);

            return new PooledConnection(cfg, this);
        } catch (Exception e) {
            logger.error("Failed to create db connection", e);
        }

        return null;
    }

}
