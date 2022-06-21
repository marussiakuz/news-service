package ru.newsservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import ru.newsservice.model.News;
import ru.newsservice.model.NewsAggregator;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.util.*;

public class HowToGetAllNews {

    public static List<News> getAllNews(NewsAggregator newsAggregator) {
        List<News> news = new ArrayList<>();

        HowToGetAllNews app = new HowToGetAllNews();

        Map<String, String> sources = app.getSources();

        for (String url : sources.values()) {
            List<News> newsFromOneChannel = getNewsFromOneChannel(url, newsAggregator.getHours(), newsAggregator.getKeyWords());
            if (!newsFromOneChannel.isEmpty()) news.addAll(newsFromOneChannel);
        }
        return news;
    }

    private static List<News> getNewsFromOneChannel(String url, int hours, List<String> keyWords) {
        List<News> news = new ArrayList<>();

        HowToGetAllNews app = new HowToGetAllNews();

        SyndFeed feed = app.feedFromUrl(url);

        if (feed == null) return news;

        List<SyndEntry> entries = feed.getEntries();

        long millis = Instant.now().toEpochMilli();
        long hoursToMillis = hours * 3_600_000L;

        for (SyndEntry entry : entries) {
            if (entry.getPublishedDate().getTime() < (millis - hoursToMillis)) continue;
            if (app.doesContainAllKeyWords((entry.getTitle() + entry.getDescription()), keyWords))
                news.add(News.builder()
                        .title(entry.getTitle())
                        .desc(entry.getDescription().getValue())
                        .link(entry.getLink())
                        .date(entry.getPublishedDate())
                        .build());
        }
        return news;
    }

    private boolean doesContainAllKeyWords(String str, List<String> keyWords) {
        for (String keyWord : keyWords) {
            if (!str.toLowerCase(Locale.ROOT).contains(keyWord.toLowerCase(Locale.ROOT))) return false;
        }
        return true;
    }

    /**
     * Считывает из yaml файла адреса rss и возвращает их как значения Map
     */
    private Map<String, String> getSources() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            return mapper.readValue(
                            HowToGetAllNews.class.getResource("/sources.yml"),
                            SourceList.class)
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
    private SyndFeed feedFromUrl(String url) {
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
