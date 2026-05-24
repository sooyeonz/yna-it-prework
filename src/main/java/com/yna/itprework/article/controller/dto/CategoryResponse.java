package com.yna.itprework.article.controller.dto;

import com.yna.itprework.article.CategoryType;

public record CategoryResponse(String key, String name) {

    public static CategoryResponse from(CategoryType category) {
        return new CategoryResponse(category.name(), category.getName());
    }
}
