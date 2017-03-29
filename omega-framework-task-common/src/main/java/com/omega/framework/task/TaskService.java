package com.omega.framework.task;

import com.omega.framework.task.bean.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by jackychenb on 11/12/2016.
 */

@FeignClient(value = "omega-framework-taskserver", fallback = TaskService.HystrixTaskService.class)
public interface TaskService {

    @RequestMapping(value = "/schedule", method = RequestMethod.POST)
    void scheduleTask(@RequestBody Task task);

    @RequestMapping("/unschedule/{id}")
    void unscheduleTask(@PathVariable("id") String id);

    @Component
    public static class HystrixTaskService implements TaskService {

        private static final Logger logger = LoggerFactory.getLogger(HystrixTaskService.class);

        @Override
        public void scheduleTask(@RequestBody Task task) {
            logger.error("failed to schedule task: " + task.id);
        }

        @Override
        public void unscheduleTask(@PathVariable("id") String id) {
            logger.error("failed to unscheudle task: " + id);
        }

    }

}
