package ru.newsservice.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import ru.newsservice.model.News;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NewsFinderImpl implements NewsFinder {

    private static final Map<String, String> SOURCES = getSources();

    @Override
    @Cacheable(value="news")
    public List<News> findAllNews(long minMillis) {
        log.info("searches for all the news for the last {} hours",
                (Instant.now().toEpochMilli() - minMillis)/3_600_000L);
        return SOURCES.values().stream()
                .map(url -> getNewsFromOneChannel(url, minMillis))
                .filter(news -> !news.isEmpty())
                .flatMap(List::stream)
                .sorted((n1, n2) -> n2.getDate().compareTo(n1.getDate()))
                .collect(Collectors.toList());
    }
    private static List<News> getNewsFromOneChannel(String url, long minMillis) {
        List<News> news = new ArrayList<>();

        SyndFeed feed = feedFromUrl(url);

        if (feed == null) return news;

        List<SyndEntry> entries = feed.getEntries();

        for (SyndEntry entry : entries) {
            if (entry.getPublishedDate().getTime() < minMillis) continue;
            news.add(News.builder()
                    .title(entry.getTitle())
                    .desc(entry.getDescription() == null? "" : entry.getDescription().getValue())
                    .link(entry.getLink())
                    .date(entry.getPublishedDate())
                    .build());
        }
        return news;
    }

    /**
     * Считывает из yaml файла адреса rss и возвращает их как значения Map
     */
    private static Map<String, String> getSources() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            return mapper.readValue(
                            NewsFinderImpl.class.getResource("/sources.yml"),
                            NewsFinderImpl.SourceList.class)
                    .getSources();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получает объект SyndFeed загружая rss из переданного url
     *
     * @param url
     * @return SyndFeed - объект из пакета rome
     */
    private static SyndFeed feedFromUrl(String url) {
        final int TIMEOUT = 1000;
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setConnectTimeout(TIMEOUT);
            XmlReader reader = new XmlReader(conn); // reader из пакета rome
            return new SyndFeedInput().build(reader);
        } catch (IOException e) {
            System.out.println("failed to connect - " + url + " skipped");
            return null;
        } catch (FeedException | NullPointerException e) {
            System.out.println("failed to parse response from - " + url + " skipped");
            return null;
        }
    }

    /**
     * Вспомогательный класс для десериализации из yaml файла
     */
    static class SourceList {
        Map<String, String> sources;

        public Map<String, String> getSources() {
            return sources;
        }
    }
}
