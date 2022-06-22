package ru.newsservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.newsservice.model.News;
import ru.newsservice.model.NewsRequest;
import ru.newsservice.repository.NewsFinder;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NewsAggregatorServiceImpl implements NewsAggregatorService {

    private final NewsFinder newsFinder;

    @Autowired
    public NewsAggregatorServiceImpl(NewsFinder newsFinder) {
        this.newsFinder = newsFinder;
    }

    @Override
    public List<News> getTheRequestedNews(NewsRequest newsRequest) {
        log.info("getting news no more than {} hours ago containing the following keywords: {}", newsRequest.getHours(),
                String.join(", ", newsRequest.getKeywords()));
        long currentMillis = Instant.now().toEpochMilli();
        long hoursToMillis = newsRequest.getHours() * 3_600_000L;
        long minEpochMilli = currentMillis - hoursToMillis;

        return newsFinder.findAllNews(minEpochMilli).stream()
                .filter(news -> doesContainAllKeyWords(news, newsRequest.getKeywords()))
                .collect(Collectors.toList());
    }

    private boolean doesContainAllKeyWords(News news, List<String> keyWords) {
        if (keyWords == null || keyWords.isEmpty()) return false;
        for (String keyWord : keyWords) {
            if (!news.getTitle().toLowerCase(Locale.ROOT).contains(keyWord.toLowerCase(Locale.ROOT))
                    || !news.getDesc().toLowerCase(Locale.ROOT).contains(keyWord.toLowerCase(Locale.ROOT)))
                return false;
        }
        return true;
    }
}
