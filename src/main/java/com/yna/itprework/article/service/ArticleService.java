package com.yna.itprework.article.service;

import com.yna.itprework.article.entity.Article;
import com.yna.itprework.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    @Value("${rss.max-article-count}")
    private int maxArticleCount;

    private final ArticleRepository articleRepository;

    /**
     * 신규 기사만 필터링하여 저장, 최대 기사 수 초과 시 오래된 기사 삭제
     */
    @Transactional
    public List<Article> saveNewArticles(List<Article> articles) {
        List<Article> newArticles = filterDuplicates(articles);
        if (newArticles.isEmpty()) {
            return List.of();
        }

        articleRepository.saveAll(newArticles);
        deleteOldArticlesIfExceedsLimit();

        log.debug("기사 저장 완료 - 신규: {}건", newArticles.size());
        return newArticles;
    }

    /**
     * 이미 저장된 기사를 제외한 신규 기사만 반환
     */
    private List<Article> filterDuplicates(List<Article> articles) {
        if (articles.isEmpty()) {
            return List.of();
        }
        
        Set<String> incomingIds = articles.stream()
                .map(Article::getArticleId)
                .collect(Collectors.toSet());

        Set<String> existingIds = articleRepository.findExistingIds(incomingIds);

        return articles.stream()
                .filter(a -> !existingIds.contains(a.getArticleId()))
                .toList();
    }

    /**
     * 저장 기사 수가 최대치를 초과하면 오래된 기사부터 삭제
     */
    private void deleteOldArticlesIfExceedsLimit() {
        long count = articleRepository.count();
        if (count <= maxArticleCount) {
            return;
        }

        int deleteCount = (int) (count - maxArticleCount);
        List<String> idsToDelete = articleRepository.findOldestArticleIds(PageRequest.of(0, deleteCount));

        articleRepository.deleteAllById(idsToDelete);
        log.debug("오래된 기사 삭제: {}건", idsToDelete.size());
    }
}