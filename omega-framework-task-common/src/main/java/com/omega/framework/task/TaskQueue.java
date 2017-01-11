package com.omega.framework.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omega.framework.task.bean.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Date;

/**
 * Created by jackychenb on 11/12/2016.
 */

@Component
public class TaskQueue {

    @Autowired
    private TaskService taskService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${task.tableName:Task}")
    private String taskTableName;

    public String addTask(final Task task) {
        checkTask(task);

        String dataMapString = null;
        if (task.dataMap != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                dataMapString = mapper.writeValueAsString(task.dataMap);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        jdbcTemplate.update("insert into " + taskTableName
                + "(id, type, triggerTime, dataMap, createTime) values(?, ?, ?, ?, now())", new Object[] {
                task.id, task.type, task.triggerTime, dataMapString
        });

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronizationAdapter() {
                        public void afterCommit() {
                            taskService.scheduleTask(task);
                        }
                    });
        } else {
            taskService.scheduleTask(task);
        }

        return task.id;
    }

    protected void checkTask(Task task) {
        if (StringUtils.isBlank(task.id)) {
            throw new IllegalArgumentException("The task id is required");
        }

        if (task.type == null || !task.type.matches("[a-zA-Z_]+\\w*")) {
            throw new IllegalArgumentException("Illegal task type: " + task.type);
        }

        if (task.triggerTime == null) {
            task.triggerTime = new Date();
        }
    }

    public void removeTask(final String id) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronizationAdapter() {
                        public void afterCommit() {
                            taskService.unscheduleTask(id);
                        }
                    });
        } else {
            taskService.unscheduleTask(id);
        }

        jdbcTemplate.update("delete from " + taskTableName + " where id=?",
                new Object[] { id });
    }

}
