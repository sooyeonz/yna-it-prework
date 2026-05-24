package com.yna.itprework.rss;

import com.yna.itprework.article.CategoryType;
import com.yna.itprework.article.entity.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RssCollector {

    private final RssParser rssParser;

    /**
     * 전체 카테고리 RSS 파싱 결과 반환
     */
    public List<Article> collectAll() {
        List<Article> articles = new ArrayList<>();
        for (CategoryType category : CategoryType.values()) {
            articles.addAll(collect(category));
        }
        return articles;
    }

    private List<Article> collect(CategoryType category) {
        List<Article> articles = rssParser.parse(category);
        if (articles.isEmpty()) {
            log.debug("수집된 기사 없음 - category: {}", category.name());
        }
        return articles;
    }
}