package com.yna.itprework.scheduler;

import com.yna.itprework.article.entity.Article;
import com.yna.itprework.article.service.ArticleService;
import com.yna.itprework.notification.service.PushService;
import com.yna.itprework.rss.RssCollector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticlesScheduler {

    private final RssCollector rssCollector;
    private final ArticleService articleService;
    private final PushService pushService;

    /**
     * 10분 주기로 전체 카테고리 RSS 수집 후 푸시 발송
     */
    @Scheduled(fixedRateString = "${rss.schedule-rate}")
    public void collectAndPush() {
        log.debug("RSS 수집 시작");
        List<Article> parsed = rssCollector.collectAll();
        List<Article> newArticles = articleService.saveNewArticles(parsed);
        log.debug("RSS 수집 종료 - 신규: {}건", newArticles.size());

        if (newArticles.isEmpty()) {
            return;
        }

        pushService.sendPushNotifications(newArticles);
    }
}
