package com.yna.itprework.scheduler;

import com.yna.itprework.rss.RssCollector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticlesScheduler {

    private final RssCollector rssCollector;

    /**
     * 10분 주기로 전체 카테고리 RSS 수집
     */
    @Scheduled(fixedRateString = "${rss.schedule-rate}")
    public void collectRss() {
        log.debug("RSS 수집 시작");
        rssCollector.collectAll();
        log.debug("RSS 수집 종료");
    }
}
