package com.yna.itprework.rss;

import com.yna.itprework.article.CategoryType;
import com.yna.itprework.article.entity.Article;
import com.yna.itprework.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RssCollector {

    private final RssParser rssParser;
    private final ArticleService articleService;

    /**
     * 전체 카테고리 RSS 수집
     */
    public void collectAll() {
        for (CategoryType category : CategoryType.values()) {
            collect(category);
        }
    }

    /**
     * 단일 카테고리 RSS 수집
     */
    private void collect(CategoryType category) {
        List<Article> articles = rssParser.parse(category);
        if (articles.isEmpty()) {
            log.debug("수집된 기사 없음 - category: {}", category.name());
            return;
        }

        articleService.saveNewArticles(articles);
        log.debug("RSS 수집 완료 - category: {}", category.name());
    }
}