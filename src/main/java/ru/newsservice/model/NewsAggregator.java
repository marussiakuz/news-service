package ru.newsservice.model;

import lombok.*;

import java.util.List;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsAggregator {
    private List<String> keyWords;
    private Integer hours;
}
