package com.yna.itprework.article.entity;

import com.yna.itprework.article.CategoryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
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

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private LocalDateTime pubDate;

    public static Article of(String articleId, String title, CategoryType category,
                             String link, String author, LocalDateTime pubDate) {
        return new Article(articleId, title, category, link, author, pubDate);
    }
}
