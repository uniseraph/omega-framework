package com.omega.framework.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omega.framework.task.bean.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by jackychenb on 11/12/2016.
 */

@RestController
@Configuration
public class AsyncTaskScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AsyncTaskScheduler.class);

    @Value("${task.scheduler.loadInterval:10}")
    private int loadInterval = 10;

    @Autowired
    private JdbcTemplate jdbcTemplate; // TODO: 需要遍历所有的数据库中的Task表

    @Value("${task.tableName:Task}")
    private String taskTableName = "Task";

    @Autowired
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${task.scheduler.exchangeName:task}")
    private String exchangeName = "task";

    @Autowired
    private RabbitAdmin rabbitAdmin;

    private volatile long endTime;
    private final Map<String, ScheduledFuture> taskMap = new ConcurrentHashMap<String, ScheduledFuture>();

    private final Map<String, Boolean> queueNameSet = new ConcurrentHashMap<String, Boolean>();

    @RequestMapping(value="/schedule", method = RequestMethod.POST)
    public void scheduleTask(@RequestBody Task task) {
        if (task.triggerTime.getTime() >= endTime) {
            return;
        }

        ScheduledFuture oldTask = taskMap.get(task.id);
        if (oldTask != null) {
            return;
        }

        if (!queueNameSet.containsKey(task.type)) {
            synchronized (rabbitAdmin) {
                Queue queue = new Queue(task.type);
                DirectExchange exchange = new DirectExchange(exchangeName);
                Binding binding = BindingBuilder.bind(queue).to(exchange).with(task.type);
                rabbitAdmin.declareQueue(queue);
                rabbitAdmin.declareExchange(exchange);
                rabbitAdmin.declareBinding(binding);

                queueNameSet.put(task.type, Boolean.TRUE);
            }
        }

        long delay = task.triggerTime.getTime() - new Date().getTime();
        if (delay < 0) {
            delay = 0;
        }

        ScheduledFuture future = scheduledThreadPoolExecutor.schedule(new ScheduledTask(task),
                delay, TimeUnit.MILLISECONDS);
        taskMap.put(task.id, future);
    }

    @RequestMapping("/unschedule/{id}")
    public void unscheudleTask(@PathVariable("id") String id) {
        ScheduledFuture future = taskMap.get(id);
        if (future == null) {
            return;
        }

        scheduledThreadPoolExecutor.remove((Runnable) future);
        taskMap.remove(id);
    }

    @Scheduled(cron = "0 0/${task.scheduler.loadInterval:10} * * * ?")
    public void schedule() {
        endTime = new Date().getTime() + loadInterval * 60 * 1000;
        taskMap.clear();

        Date triggerTime = new Date(endTime);
        String startId = "0";
        List<Map<String, Object>> taskList;
        do {
//            taskList = jdbcTemplate.queryForList("select * from " + taskTableName +
//                            " where triggerTime<? and id>? " +
//                            " order by id " +
//                            " limit 100", new Object[] { triggerTime, startId });

            // 为了兼容MySQL与Oracle，不分页了
            taskList = jdbcTemplate.queryForList("select * from " + taskTableName +
                    " where triggerTime<?", new Object[] { triggerTime });

            ObjectMapper mapper = new ObjectMapper();
            for (Map<String, Object> m : taskList) {
                Task t = new Task();
                t.id = (String) m.get("id");
                t.type = (String) m.get("type");
                t.triggerTime = (Date) m.get("triggerTime");

                String dataMapString = (String) m.get("dataMap");
                if (StringUtils.isNotBlank(dataMapString)) {
                    try {
                        t.dataMap = mapper.readValue(dataMapString, HashMap.class);
                    } catch (IOException e) {
                        logger.error("failed to read data map of task " + t.id, e);
                        continue;
                    }
                }

                scheduleTask(t);
                startId = t.id;
            }

            // 为了兼容MySQL与Oracle，不分页了
            break;
        } while (taskList.size() > 0);
    }

    private class ScheduledTask implements Runnable {

        private final Task task;
        public ScheduledTask(Task task) {
            this.task = task;
        }

        public void run() {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String taskString = mapper.writeValueAsString(task);
                rabbitTemplate.convertAndSend(exchangeName, task.type, taskString);
            } catch (Exception e) {
                logger.error("failed to dispatch task " + task.id, e);
            }
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (!(obj instanceof ScheduledTask)) {
                return false;
            }

            return ((ScheduledTask) obj).task.equals(task);
        }

        public int hashCode() {
            return task.hashCode();
        }

    }

}
