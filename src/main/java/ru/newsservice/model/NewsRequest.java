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
public class NewsRequest {
    private List<String> keywords;
    private int hours;
}
