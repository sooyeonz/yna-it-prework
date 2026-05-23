package com.yna.itprework.article.entity;

import com.yna.itprework.article.CategoryType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "articles")
public class Article {

    @Id
    @Column(name = "article_id")
    private String articleId;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType category;

    @Column(nullable = false)
    private String link;

    private String author;

    @Column(nullable = false)
    private LocalDateTime pubDate;

}
