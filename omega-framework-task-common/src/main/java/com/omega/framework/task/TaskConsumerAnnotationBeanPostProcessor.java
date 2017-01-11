package com.omega.framework.task;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jackychenb on 12/12/2016.
 */

public class TaskConsumerAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final TaskConsumerRegistry taskConsumerRegistry;
    public TaskConsumerAnnotationBeanPostProcessor(TaskConsumerRegistry taskConsumerRegistry) {
        this.taskConsumerRegistry = taskConsumerRegistry;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        ReflectionUtils.doWithMethods(targetClass, new ReflectionUtils.MethodCallback() {

            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                for (TaskConsumer consumer : findTaskConsumerAnnotations(method)) {
                    processAmqpListener(consumer, method, bean, beanName);
                }
            }
        }, ReflectionUtils.USER_DECLARED_METHODS);

        return bean;
    }

    private Collection<TaskConsumer> findTaskConsumerAnnotations(Method method) {
        Set<TaskConsumer> consumers = new HashSet<TaskConsumer>();
        TaskConsumer ann = AnnotationUtils.findAnnotation(method, TaskConsumer.class);
        if (ann != null) {
            consumers.add(ann);
        }

        return consumers;
    }

    protected void processAmqpListener(TaskConsumer consumer, Method method, Object bean, String beanName) {
        Method methodToUse = checkProxy(method, bean);
        taskConsumerRegistry.register(consumer, bean, methodToUse);
    }

    private Method checkProxy(Method method, Object bean) {
        if (AopUtils.isJdkDynamicProxy(bean)) {
            try {
                // Found a @TaskConsumer method on the target class for this JDK proxy ->
                // is it also present on the proxy itself?
                method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
                Class<?>[] proxiedInterfaces = ((Advised) bean).getProxiedInterfaces();
                for (Class<?> iface : proxiedInterfaces) {
                    try {
                        method = iface.getMethod(method.getName(), method.getParameterTypes());
                        break;
                    }
                    catch (NoSuchMethodException noMethod) {
                    }
                }
            } catch (SecurityException ex) {
                ReflectionUtils.handleReflectionException(ex);
            } catch (NoSuchMethodException ex) {
                throw new IllegalStateException(String.format(
                        "@TaskConsumer method '%s' found on bean target class '%s', " +
                                "but not found in any interface(s) for bean JDK proxy. Either " +
                                "pull the method up to an interface or switch to subclass (CGLIB) " +
                                "proxies by setting proxy-target-class/proxyTargetClass " +
                                "attribute to 'true'", method.getName(), method.getDeclaringClass().getSimpleName()));
            }
        }

        return method;
    }

}
