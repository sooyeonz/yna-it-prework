package com.yna.itprework.article.repository;

import com.yna.itprework.article.entity.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ArticleRepository extends JpaRepository<Article, String> {

    @Query("SELECT a.articleId FROM Article a WHERE a.articleId IN :ids")
    Set<String> findExistingIds(@Param("ids") Set<String> ids);

    @Query("SELECT a.articleId FROM Article a ORDER BY a.pubDate ASC")
    List<String> findOldestArticleIds(Pageable pageable);
}
