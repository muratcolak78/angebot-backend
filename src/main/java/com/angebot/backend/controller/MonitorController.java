package com.angebot.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
@Slf4j
public class MonitorController {
    
    private final ThreadPoolTaskExecutor mailTaskExecutor;
    private final ThreadPoolTaskExecutor pdfTaskExecutor;
    
    @GetMapping("/thread-pools")
    public Map<String, Object> getThreadPoolStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // Status des Mailpools
        Map<String, Object> mailPool = new HashMap<>();
        mailPool.put("activeCount", mailTaskExecutor.getActiveCount());
        mailPool.put("corePoolSize", mailTaskExecutor.getCorePoolSize());
        mailPool.put("maxPoolSize", mailTaskExecutor.getMaxPoolSize());
        mailPool.put("queueSize", mailTaskExecutor.getThreadPoolExecutor().getQueue().size());
        mailPool.put("completedTaskCount", mailTaskExecutor.getThreadPoolExecutor().getCompletedTaskCount());
        
        // PDF-Pool-Status
        Map<String, Object> pdfPool = new HashMap<>();
        pdfPool.put("activeCount", pdfTaskExecutor.getActiveCount());
        pdfPool.put("corePoolSize", pdfTaskExecutor.getCorePoolSize());
        pdfPool.put("maxPoolSize", pdfTaskExecutor.getMaxPoolSize());
        pdfPool.put("queueSize", pdfTaskExecutor.getThreadPoolExecutor().getQueue().size());
        
        status.put("mailPool", mailPool);
        status.put("pdfPool", pdfPool);
        status.put("timestamp", System.currentTimeMillis());
        
        log.info("Thread pool durumu: {}", status);
        
        return status;
    }
    
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "UP",
            "service", "angebot-backend",
            "timestamp", String.valueOf(System.currentTimeMillis())
        );
    }
}