package ru.newsservice.service;

import ru.newsservice.model.News;
import ru.newsservice.model.NewsRequest;

import java.util.List;

public interface NewsAggregatorService {
    public List<News> getTheRequestedNews(NewsRequest newsRequest);
}
