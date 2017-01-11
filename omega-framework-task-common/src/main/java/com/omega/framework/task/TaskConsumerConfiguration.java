package com.omega.framework.task;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by jackychenb on 12/12/2016.
 */

@Configuration
public class TaskConsumerConfiguration {

    @Bean
    public TaskConsumerAnnotationBeanPostProcessor taskConsumerAnnotationBeanPostProcessor(
            TaskConsumerRegistry taskConsumerRegistry) {

        return new TaskConsumerAnnotationBeanPostProcessor(taskConsumerRegistry);
    }

    @Bean
    public TaskConsumerRegistry taskConsumerRegistry(ConnectionFactory connectionFactory,
                                                     TaskConsumerInvoker taskConsumerInvoker) {

        return new TaskConsumerRegistry(connectionFactory, taskConsumerInvoker);
    }

}
