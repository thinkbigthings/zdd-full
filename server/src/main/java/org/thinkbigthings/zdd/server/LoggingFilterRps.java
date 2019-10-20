package org.thinkbigthings.zdd.server;

import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.servlet.*;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static java.time.Instant.now;


@Component
public class LoggingFilterRps implements Filter {

    private final String legend = " reqs, avg-ms, max-ms: [";
    private final Runnable logger = () -> log(concurrentCopyAndClear());
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
    private final ConcurrentHashMap<Long, AtomicLong> timeToRequestCount = new ConcurrentHashMap();

    public LoggingFilterRps() {
        executor.scheduleAtFixedRate(logger, 0, 1, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        long startTime = System.currentTimeMillis();
        chain.doFilter(req, res);
        long elapsed = System.currentTimeMillis() - startTime;

        executor.submit(() -> accumulateStatistic(elapsed));
    }

    private void accumulateStatistic(Long elapsed) {
        timeToRequestCount.computeIfAbsent(elapsed, b -> new AtomicLong(0L));
        timeToRequestCount.get(elapsed).incrementAndGet();
    }

    private void log(Map<Long,Long> timeToRequestCountCopy) {

        Instant logTime = now();

        var maxTimeMs = timeToRequestCountCopy.entrySet().stream()
                .filter(e -> e.getValue() != 0L)
                .mapToLong(e -> e.getKey())
                .max()
                .orElse(0L);

        var numRequests = timeToRequestCountCopy.values().stream()
                .mapToLong(Long::valueOf)
                .sum();

        var totalTime = timeToRequestCountCopy.entrySet().stream()
                .map(e -> e.getKey() * e.getValue())
                .mapToLong(Long::valueOf)
                .sum();

        var avgResponseTime = Math.round((double)totalTime / (double)numRequests);

        System.out.println(logTime + legend + numRequests + ", " + avgResponseTime + ", " + maxTimeMs + "]");
    }

    // copy and clear values atomically without locking the map
    // then can work on the copy without synchronization
    private Map<Long,Long> concurrentCopyAndClear() {

        Map<Long,Long> copy = new HashMap<>();
        timeToRequestCount.forEachEntry(1024, entry -> {
            long requestCount = entry.getValue().getAndSet(0L);
            long timeBin = entry.getKey();
            copy.put(timeBin, requestCount);
        });

        return copy;
    }

}
