package com.jne.service.utils;



import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommandMetrics {

	private static final Logger log = LoggerFactory.getLogger(KafkaCommandMetrics.class);

    // topic -> metrics (consumed/saved)
    private final Map<String, TopicMetrics> topicMetrics = new ConcurrentHashMap<>();
    private volatile LocalDate currentDay = LocalDate.now();

    /**
     * Record number of messages consumed from Kafka for a topic
     */
    public void incrementConsumed(String topic, int count) {
        checkDayChange();
        topicMetrics.computeIfAbsent(topic, t -> new TopicMetrics()).consumed.addAndGet(count);
    }

    /**
     * Record number of messages successfully saved to DB for a topic
     */
    public void incrementSaved(String topic, int count) {
        checkDayChange();
        topicMetrics.computeIfAbsent(topic, t -> new TopicMetrics()).saved.addAndGet(count);
    }

    /**
     * Get full metrics snapshot in desired format:
     * {
     *   "topicA": { "consumed": 5000, "saved": 4800 },
     *   "topicB": { "consumed": 12000, "saved": 11900 }
     * }
     */
    public Map<String, Map<String, Integer>> getAllMetrics() {
        Map<String, Map<String, Integer>> snapshot = new ConcurrentHashMap<>();
        topicMetrics.forEach((topic, metrics) -> {
            Map<String, Integer> values = new ConcurrentHashMap<>();
            values.put("consumed", metrics.consumed.get());
            values.put("saved", metrics.saved.get());
            snapshot.put(topic, values);
        });
        return snapshot;
    }

    /**
     * Reset all topic metrics at midnight or on app restart
     */
    private void resetAll() {
        topicMetrics.clear();
    }

    /**
     * Check if the day has changed and reset if needed
     */
    private void checkDayChange() {
        LocalDate now = LocalDate.now();
        if (!now.equals(currentDay)) {
            synchronized (this) {
                if (!now.equals(currentDay)) {
                    log.info("📆 Day changed — resetting all Kafka metrics");
                    resetAll();
                    currentDay = now;
                }
            }
        }
    }

    /**
     * Inner holder for topic metrics
     */
    private static class TopicMetrics {
        AtomicInteger consumed = new AtomicInteger(0);
        AtomicInteger saved = new AtomicInteger(0);
    }
}