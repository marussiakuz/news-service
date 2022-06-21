package ru.newsservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.newsservice.model.News;
import ru.newsservice.model.NewsAggregator;
import ru.newsservice.service.NewsAggregatorService;

import java.util.List;

@RestController
@RequestMapping
public class NewsAggregatorController {

    private final NewsAggregatorService newsAggregatorService;

    public NewsAggregatorController(NewsAggregatorService newsAggregatorService) {
        this.newsAggregatorService = newsAggregatorService;
    }

    @PostMapping
    public List<News> add(@RequestBody NewsAggregator newsAggregator) {
        return newsAggregatorService.getNews(newsAggregator);
    }
}
