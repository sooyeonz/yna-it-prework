package com.yna.itprework.article.controller.dto;

import com.yna.itprework.article.entity.Article;

import java.time.LocalDateTime;

public record ArticleResponse(
        String articleId,
        String title,
        String category,
        String link,
        String author,
        LocalDateTime pubDate,
        boolean isRead
) {
    public static ArticleResponse from(Article article) {
        return new ArticleResponse(
                article.getArticleId(),
                article.getTitle(),
                article.getCategory().name(),
                article.getLink(),
                article.getAuthor(),
                article.getPubDate(),
                article.isRead()
        );
    }
}