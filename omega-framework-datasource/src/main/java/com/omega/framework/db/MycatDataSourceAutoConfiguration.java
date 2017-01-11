package com.omega.framework.db;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryPolicyFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRetryProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by jackychenb on 24/12/2016.
 */

@Configuration
@EnableConfigurationProperties(MycatDataSourceProperties.class)
public class MycatDataSourceAutoConfiguration {

    protected <T> T createDataSource(MycatDataSourceProperties properties,
                                     Class<? extends DataSource> type) {
        return (T) DataSourceBuilder.create(properties.getClassLoader()).type(type)
                .driverClassName(properties.determineDriverClassName())
                .url(properties.determineUrl())
                .username(properties.determineUsername())
                .password(properties.determinePassword()).build();
    }

    @Bean
    @ConfigurationProperties("mycat")
    public MycatLocator mycatLocator(
            LoadBalancerClient loadBalancerClient,
            RetryTemplate retryTemplate,
            LoadBalancerRetryProperties properties,
            LoadBalancedRetryPolicyFactory lbRetryPolicyFactory) {

        return new MycatLocator(loadBalancerClient, retryTemplate, properties, lbRetryPolicyFactory);
    }

    @Bean
    @ConfigurationProperties("mycat.datasource.tomcat")
    public DataSource dataSource(MycatDataSourceProperties properties, MycatLocator mycatLocator) {
        MycatDataSource dataSource = createDataSource(properties, MycatDataSource.class);

        PoolProperties poolProperties = (PoolProperties) dataSource.getPoolProperties();
        MycatPoolProperties mycatPoolProperties = new MycatPoolProperties();
        BeanUtils.copyProperties(poolProperties, mycatPoolProperties);
        mycatPoolProperties.setMycatLocator(mycatLocator);
        dataSource.setPoolProperties(mycatPoolProperties);

        DatabaseDriver databaseDriver = DatabaseDriver.fromJdbcUrl(properties.determineUrl());
        String validationQuery = databaseDriver.getValidationQuery();
        if (validationQuery != null) {
            dataSource.setTestOnBorrow(true);
            dataSource.setValidationQuery(validationQuery);
        }

        return dataSource;
    }

}
