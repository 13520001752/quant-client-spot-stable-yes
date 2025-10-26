package com.magic.service;

import com.magic.controller.ServiceController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScheduledTask {

    @Scheduled(fixedDelay = 60 * 1000)
    public void miniTickerMonitor() {
        log.info("checkWebSocketStatus hashmapQuantTask num:{}", ServiceController.hashmapQuantTask.size());

        if (ServiceController.hashmapQuantTask.isEmpty()) {
            return;
        }

        ServiceController.hashmapQuantTask.forEach((configId, quantTask) -> {
            if (quantTask == null) {
                return;
            }
            log.info("checkWebSocketStatus configId:{}", configId);
            quantTask.checkWebSocketStatus();
        });
    }
}
