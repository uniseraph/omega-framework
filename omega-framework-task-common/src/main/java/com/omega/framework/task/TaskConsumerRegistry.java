package com.omega.framework.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.*;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jackychenb on 12/12/2016.
 */

public class TaskConsumerRegistry implements DisposableBean, SmartLifecycle, ApplicationContextAware,
        ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(TaskConsumerRegistry.class);

    private final Map<String, MessageListenerContainer> listenerContainers =
            new ConcurrentHashMap<String, MessageListenerContainer>();

    private int phase = Integer.MAX_VALUE;

    private ConfigurableApplicationContext applicationContext;

    private boolean contextRefreshed;

    private final ConnectionFactory connectionFactory;
    private final TaskConsumerInvoker taskConsumerInvoker;
    public TaskConsumerRegistry(ConnectionFactory connectionFactory,
                                TaskConsumerInvoker taskConsumerInvoker) {

        this.connectionFactory = connectionFactory;
        this.taskConsumerInvoker = taskConsumerInvoker;
    }

    public void register(TaskConsumer consumer, final Object bean, final Method method) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(consumer.value());
        container.setMessageListener(new MessageListener() {

            @Override
            public void onMessage(Message message) {
                taskConsumerInvoker.invoke(message, bean, method);
            }
        });

        listenerContainers.put(getIdForContainer(bean, method), container);
    }

    protected String getIdForContainer(Object bean, Method method) {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        return targetClass.getName() + "#" + method.getName();
    }

    public Collection<MessageListenerContainer> getListenerContainers() {
        return Collections.unmodifiableCollection(this.listenerContainers.values());
    }

    @Override
    public void destroy() throws Exception {
        for (MessageListenerContainer listenerContainer : getListenerContainers()) {
            if (listenerContainer instanceof DisposableBean) {
                try {
                    ((DisposableBean) listenerContainer).destroy();
                }
                catch (Exception e) {
                    logger.warn("failed to destroy message listener container", e);
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            this.applicationContext = (ConfigurableApplicationContext) applicationContext;
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().equals(this.applicationContext)) {
            this.contextRefreshed = true;
        }
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        Collection<MessageListenerContainer> listenerContainers = getListenerContainers();
        AggregatingCallback aggregatingCallback = new AggregatingCallback(listenerContainers.size(), callback);
        for (MessageListenerContainer listenerContainer : listenerContainers) {
            listenerContainer.stop(aggregatingCallback);
        }
    }

    @Override
    public void start() {
        for (MessageListenerContainer listenerContainer : getListenerContainers()) {
            startIfNecessary(listenerContainer);
        }
    }

    @Override
    public void stop() {
        for (MessageListenerContainer listenerContainer : getListenerContainers()) {
            listenerContainer.stop();
        }
    }

    @Override
    public boolean isRunning() {
        for (MessageListenerContainer listenerContainer : getListenerContainers()) {
            if (listenerContainer.isRunning()) {
                return true;
            }
        }

        return false;
    }

    private void startIfNecessary(MessageListenerContainer listenerContainer) {
        if (this.contextRefreshed || listenerContainer.isAutoStartup()) {
            listenerContainer.start();
        }
    }

    @Override
    public int getPhase() {
        return phase;
    }

    private static final class AggregatingCallback implements Runnable {

        private final AtomicInteger count;

        private final Runnable finishCallback;

        private AggregatingCallback(int count, Runnable finishCallback) {
            this.count = new AtomicInteger(count);
            this.finishCallback = finishCallback;
        }

        @Override
        public void run() {
            if (this.count.decrementAndGet() == 0) {
                this.finishCallback.run();
            }
        }

    }

}

