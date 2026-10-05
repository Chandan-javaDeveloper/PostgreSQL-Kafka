package com.jne.controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jne.service.utils.KafkaCommandMetrics;

@RestController
@RequestMapping("/metrics")
public class MetricsController {

    @Autowired
    private KafkaCommandMetrics commandMetrics;

    /**
     * Get metrics for a specific topic
     * Example:
     * GET /metrics/topic/topicA
     * → { "consumed": 5000, "saved": 4800 }
     */
    @GetMapping("/topic/{topic}")
    public Map<String, Integer> getTopicMetrics(@PathVariable String topic) {
        return commandMetrics.getAllMetrics().getOrDefault(topic, Map.of("consumed", 0, "saved", 0));
    }

    /**
     * Get metrics for all topics
     * Example:
     * GET /metrics/all
     * → {
     *      "topicA": { "consumed": 5000, "saved": 4800 },
     *      "topicB": { "consumed": 12000, "saved": 11900 }
     *    }
     */
    @GetMapping("/all")
    public Map<String, Map<String, Integer>> getAllMetrics() {
        return commandMetrics.getAllMetrics();
    }
    
}
