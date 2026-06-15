package com.queueingmesoftly.queue.domain;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
class DataInitializer implements ApplicationRunner {

    private final QueueService queueService;

    DataInitializer(QueueService queueService) {
        this.queueService = queueService;
    }

    @Override
    public void run(ApplicationArguments args) {
        queueService.initializeCourts();
    }
}

