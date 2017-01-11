package com.omega.framework.db;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

/**
 * Created by jackychenb on 25/12/2016.
 */
public class MycatLocator {

    private final LoadBalancerClient loadBalancer;
    private final RetryTemplate retryTemplate;
    private final LoadBalancerRetryProperties lbProperties;
    private final LoadBalancedRetryPolicyFactory lbRetryPolicyFactory;

    private String serviceId;
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    private ServiceInstance[] instances;
    public void setServers(String servers) {
        if (!StringUtils.isEmpty(servers)) {
            String[] pairs = servers.split("\\s*,\\s*");
            instances = new ServiceInstance[pairs.length];
            for (int i = 0; i < pairs.length; i++) {
                String[] pair = pairs[i].split("\\s*:\\s*");
                instances[i] = new DefaultServiceInstance(null, pair[0], Integer.parseInt(pair[1]), false);
            }
        }
    }

    public MycatLocator(LoadBalancerClient loadBalancer,
                        RetryTemplate retryTemplate,
                        LoadBalancerRetryProperties lbProperties,
                        LoadBalancedRetryPolicyFactory lbRetryPolicyFactory) {

        this.loadBalancer = loadBalancer;
        this.retryTemplate = retryTemplate;
        this.lbProperties = lbProperties;
        this.lbRetryPolicyFactory = lbRetryPolicyFactory;
    }

    /**
     * 获得一个可用的Mycat服务器信息
     */
    public ServiceInstance getServiceInstance() throws IOException {
        if (instances != null) {
            return getServiceInstanceFromList();
        }

        return getServiceInstanceByServiceId();
    }

    private ServiceInstance getServiceInstanceFromList() throws IOException {
        int start = new Random().nextInt(instances.length);
        int end = start + instances.length;
        for (int i = start; i < end; i++) {
            ServiceInstance instance = instances[i % instances.length];
            if (isReachable(instance.getHost(), instance.getPort())) {
                return instance;
            }
        }

        return null;
    }

    private ServiceInstance getServiceInstanceByServiceId() throws IOException {
        LoadBalancedRetryPolicy retryPolicy = lbRetryPolicyFactory.create(serviceId, loadBalancer);

        retryTemplate.setRetryPolicy(
                !lbProperties.isEnabled() || retryPolicy == null ? new NeverRetryPolicy()
                        : new InterceptorRetryPolicy(retryPolicy, loadBalancer, serviceId));

        return (ServiceInstance) retryTemplate.execute(new RetryCallback<ServiceInstance, IOException>() {

            @Override
            public ServiceInstance doWithRetry(RetryContext context) throws IOException {
                ServiceInstance serviceInstance = null;
                if (context instanceof LoadBalancedRetryContext) {
                    LoadBalancedRetryContext lbContext = (LoadBalancedRetryContext) context;
                    serviceInstance = lbContext.getServiceInstance();
                }

                if (serviceInstance == null) {
                    serviceInstance = loadBalancer.choose(serviceId);
                }

                return MycatLocator.this.loadBalancer.execute(
                        serviceId, serviceInstance, new LoadBalancerRequest<ServiceInstance>() {

                            @Override
                            public ServiceInstance apply(ServiceInstance instance) throws Exception {
                                if (isReachable(instance.getHost(), instance.getPort())) {
                                    return instance;
                                }

                                throw new IOException("Unreachable Mycat instance: " + instance);
                            }

                        });
            }
        });
    }

    protected boolean isReachable(String host, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port), 3000);
        } catch (IOException e) {
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }

        return true;
    }

}