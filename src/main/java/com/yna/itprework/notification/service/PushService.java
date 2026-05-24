package com.yna.itprework.notification.service;

import com.yna.itprework.article.CategoryType;
import com.yna.itprework.article.entity.Article;
import com.yna.itprework.notification.entity.NotificationLog;
import com.yna.itprework.notification.repository.NotificationLogRepository;
import com.yna.itprework.user.entity.User;
import com.yna.itprework.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushService {

    private final UserRepository userRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final PushNotificationService pushNotificationService;

    /**
     * 신규 기사를 카테고리별로 그룹핑 후 구독 유저에게 발송
     */
    public void sendPushNotifications(List<Article> articles) {
        Map<CategoryType, List<Article>> byCategory = articles.stream()
                .collect(Collectors.groupingBy(Article::getCategory));

        LocalTime now = LocalTime.now();
        List<NotificationLog> logs = new ArrayList<>();

        for (Map.Entry<CategoryType, List<Article>> entry : byCategory.entrySet()) {
            List<User> users = userRepository.findByCategoryWithPreferences(entry.getKey());
            logs.addAll(sendToUsers(entry.getValue(), users, now));
        }

        notificationLogRepository.saveAll(logs);
    }

    /**
     * DND 시간대 유저를 제외하고 대상 기사 전체에 대해 발송
     */
    private List<NotificationLog> sendToUsers(List<Article> articles, List<User> users, LocalTime now) {
        List<NotificationLog> logs = new ArrayList<>();
        for (User user : users) {
            if (user.isDndActive(now)) {
                log.debug("DND 시간대 발송 제외 - userId: {}", user.getId());
                continue;
            }
            for (Article article : articles) {
                logs.add(send(user, article));
            }
        }
        return logs;
    }

    /**
     * 단일 유저 + 단일 기사 발송 후 NotificationLog 반환
     */
    private NotificationLog send(User user, Article article) {
        String result = dispatch(user, article);
        log.debug("푸시 발송 - userId: {}, articleId: {}, result: {}", user.getId(), article.getArticleId(), result);
        return NotificationLog.of(user.getDeviceId(), user.getPushType(), article.getArticleId(), article.getTitle(), article.getCategory(), result);
    }

    /**
     * pushType에 해당하는 발송 메서드를 실행
     * 새로운 PushType 추가 시 컴파일 에러로 누락 방지
     */
    private String dispatch(User user, Article article) {
        return switch (user.getPushType()) {
            case APNS ->
                    pushNotificationService.sendAPNS(user.getDeviceId(), article.getArticleId(), article.getTitle());
            case FCM -> pushNotificationService.sendFCM(user.getDeviceId(), article.getArticleId(), article.getTitle());
        };
    }

}
