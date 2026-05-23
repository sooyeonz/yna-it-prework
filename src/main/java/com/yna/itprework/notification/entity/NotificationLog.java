package com.yna.itprework.notification.entity;

import com.yna.itprework.article.CategoryType;
import com.yna.itprework.user.PushType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notification_logs")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PushType pushType;

    @Column(nullable = false)
    private String articleId;

    @Column(nullable = false)
    private String articleTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType articleCategory;

    @Column(nullable = false)
    private String status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static NotificationLog of(String deviceId, PushType pushType, String articleId, String articleTitle, CategoryType articleCategory, String status) {
        return NotificationLog.builder()
                .deviceId(deviceId)
                .pushType(pushType)
                .articleId(articleId)
                .articleTitle(articleTitle)
                .articleCategory(articleCategory)
                .status(status)
                .build();
    }
}
