package ru.newsservice.repository;

import ru.newsservice.model.News;

import java.util.List;

public interface NewsFinder {

    public List<News> findAllNews(long minMillis);
}
