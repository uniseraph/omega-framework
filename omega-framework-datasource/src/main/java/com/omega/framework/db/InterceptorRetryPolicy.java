package com.omega.framework.db;

import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryContext;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryPolicy;
import org.springframework.cloud.client.loadbalancer.ServiceInstanceChooser;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;

/**
 * Created by jackychenb on 25/12/2016.
 */
public class InterceptorRetryPolicy implements RetryPolicy {

    private LoadBalancedRetryPolicy policy;
    private ServiceInstanceChooser serviceInstanceChooser;
    private String serviceId;

    /**
     * Creates a new retry policy.
     * @param policy the retry policy from the load balancer
     * @param serviceInstanceChooser the load balancer client
     * @param serviceId the id of the service
     */
    public InterceptorRetryPolicy(LoadBalancedRetryPolicy policy,
                                  ServiceInstanceChooser serviceInstanceChooser, String serviceId) {
        this.policy = policy;
        this.serviceInstanceChooser = serviceInstanceChooser;
        this.serviceId = serviceId;
    }

    @Override
    public boolean canRetry(RetryContext context) {
        LoadBalancedRetryContext lbContext = (LoadBalancedRetryContext)context;
        if(lbContext.getRetryCount() == 0  && lbContext.getServiceInstance() == null) {
            //We haven't even tried to make the request yet so return true so we do
            lbContext.setServiceInstance(serviceInstanceChooser.choose(serviceId));
            return true;
        }

        return policy.canRetryNextServer(lbContext);
    }

    @Override
    public RetryContext open(RetryContext parent) {
        return new LoadBalancedRetryContext(parent, null);
    }

    @Override
    public void close(RetryContext context) {
        policy.close((LoadBalancedRetryContext) context);
    }

    @Override
    public void registerThrowable(RetryContext context, Throwable throwable) {
        LoadBalancedRetryContext lbContext = (LoadBalancedRetryContext) context;
        //this is important as it registers the last exception in the context and also increases the retry count
        lbContext.registerThrowable(throwable);
        //let the policy know about the exception as well
        policy.registerThrowable(lbContext, throwable);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InterceptorRetryPolicy that = (InterceptorRetryPolicy) o;

        if (!policy.equals(that.policy)) return false;
        if (!serviceInstanceChooser.equals(that.serviceInstanceChooser)) return false;
        return serviceId.equals(that.serviceId);
    }

    @Override
    public int hashCode() {
        int result = serviceId.hashCode();
        result = 31 * result + policy.hashCode();
        result = 31 * result + serviceInstanceChooser.hashCode();
        return result;
    }

}
