package com.yna.itprework.article.controller;

import com.yna.itprework.article.CategoryType;
import com.yna.itprework.article.controller.dto.ArticleResponse;
import com.yna.itprework.article.controller.dto.CategoryResponse;
import com.yna.itprework.article.service.ArticleService;
import com.yna.itprework.exception.InvalidCategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 카테고리 목록 반환
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        List<CategoryResponse> categories = Arrays.stream(CategoryType.values())
                .map(CategoryResponse::from)
                .toList();
        return ResponseEntity.ok(categories);
    }

    /**
     * 카테고리별 기사 목록 반환
     */
    @GetMapping("/articles")
    public ResponseEntity<Page<ArticleResponse>> getArticles(
            @RequestParam String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ArticleResponse> articles = articleService.getArticlesByCategory(parseCategoryType(category), page, size)
                .map(ArticleResponse::from);
        return ResponseEntity.ok(articles);
    }

    /**
     * 기사 읽음 처리
     */
    @PatchMapping("/articles/{articleId}/read")
    public ResponseEntity<Void> readArticle(@PathVariable String articleId) {
        articleService.readArticle(articleId);
        return ResponseEntity.ok().build();
    }

    private CategoryType parseCategoryType(String category) {
        try {
            return CategoryType.valueOf(category);
        } catch (IllegalArgumentException e) {
            throw new InvalidCategoryException(category);
        }
    }
}
