package ru.newsservice.model;

import java.util.Date;

import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News {

    private String title;
    private String desc;
    private String link;
    private Date date;
}
