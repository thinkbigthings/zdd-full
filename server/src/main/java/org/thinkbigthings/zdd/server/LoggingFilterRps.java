package org.thinkbigthings.zdd.server;

import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.servlet.*;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static java.time.Instant.now;


@Component
public class LoggingFilterRps implements Filter {

    private final String legend = "[reqs, avg-ms, max-ms]";
    private final Runnable logger = () -> log(getAndResetStatistics());
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
    private final ConcurrentHashMap<Long, AtomicLong> timeToRequestCount = new ConcurrentHashMap<>();

    private final String timeFormat = "yyyy-MM-dd hh:mm:ss";
    private final String zone = "America/New_York";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat).withZone(ZoneId.of(zone));

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

    private void log(List<RequestDurationCount> histogram) {

        var logTime = formatter.format(now());

        var maxTimeMs = histogram.stream()
                .mapToLong(RequestDurationCount::requestDurationMs)
                .max()
                .orElse(0L);

        var totalRequests = histogram.stream()
                .mapToLong(RequestDurationCount::requestCount)
                .sum();

        var totalTime = histogram.stream()
                .mapToLong(RequestDurationCount::getTimeSpent)
                .sum();

        var avgResponseTime = Math.round((double)totalTime / (double)totalRequests);
        var requestLog = "[" + totalRequests + ", " + avgResponseTime + ", " + maxTimeMs + "]";

        System.out.println(logTime + " " + legend + ": " + requestLog);
    }

    // copy and clear values atomically without locking the map
    // then can work on the copy without synchronization
    private List<RequestDurationCount> getAndResetStatistics() {

        List<RequestDurationCount> durations = new ArrayList<>();

        timeToRequestCount.forEachEntry(1024, entry -> {
            long requestCountPerDuration = entry.getValue().getAndSet(0L);
            long requestDuration = entry.getKey();
            if(requestCountPerDuration != 0L) {
                durations.add(new RequestDurationCount(requestDuration, requestCountPerDuration));
            }
        });

        return durations;
    }

    record RequestDurationCount(long requestDurationMs, long requestCount) {
        public long getTimeSpent() {
            return requestDurationMs() * requestCount();
        }
    }
}
