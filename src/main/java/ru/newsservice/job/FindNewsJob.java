package ru.newsservice.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.newsservice.repository.NewsFinder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
public class FindNewsJob {

    //private static long currentEpochMillis = System.currentTimeMillis();
    private static long currentEpochMillis = Instant.now().toEpochMilli();
    private final static long MIN = 24 * 60 * 60 * 1000;
    private final NewsFinder newsFinder;

    @Autowired
    public FindNewsJob(NewsFinder newsFinder) {
        this.newsFinder = newsFinder;
    }

    @Scheduled(fixedDelay = 300000)
    public void findNews() {
        LocalDateTime start = LocalDateTime.now();

        log.info("Find news job started.");

        newsFinder.findAllNews(currentEpochMillis - MIN);

        LocalDateTime end = LocalDateTime.now();

        log.info("Find news job finished. Took seconds: {}",
                end.toEpochSecond(ZoneOffset.UTC) - start.toEpochSecond(ZoneOffset.UTC));
    }
}
