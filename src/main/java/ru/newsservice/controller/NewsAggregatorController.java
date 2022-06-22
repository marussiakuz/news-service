package ru.newsservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.newsservice.model.News;
import ru.newsservice.model.NewsRequest;
import ru.newsservice.service.NewsAggregatorService;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/")
public class NewsAggregatorController {

    private final NewsAggregatorService newsAggregatorService;

    @Autowired
    public NewsAggregatorController(NewsAggregatorService newsAggregatorService) {
        this.newsAggregatorService = newsAggregatorService;
    }

    @PostMapping
    public List<News> add(@RequestBody NewsRequest newsRequest) {
        return newsAggregatorService.getTheRequestedNews(newsRequest);
    }
}
