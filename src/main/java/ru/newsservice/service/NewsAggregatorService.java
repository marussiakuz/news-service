package ru.newsservice.service;

import org.springframework.stereotype.Service;
import ru.newsservice.HowToGetAllNews;
import ru.newsservice.model.News;
import ru.newsservice.model.NewsAggregator;

import java.util.List;

@Service
public class NewsAggregatorService {

    public List<News> getNews(NewsAggregator newsAggregator) {
        return HowToGetAllNews.getAllNews(newsAggregator);
    }
}
