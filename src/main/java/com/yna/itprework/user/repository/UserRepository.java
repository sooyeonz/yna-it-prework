package com.yna.itprework.user.repository;

import com.yna.itprework.article.CategoryType;
import com.yna.itprework.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.categoryPreferences cp WHERE cp.category = :category")
    List<User> findByCategoryWithPreferences(@Param("category") CategoryType category);
}
