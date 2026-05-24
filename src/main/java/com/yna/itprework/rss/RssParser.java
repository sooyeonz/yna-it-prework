package com.yna.itprework.rss;

import com.yna.itprework.article.CategoryType;
import com.yna.itprework.article.entity.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class RssParser {

    private static final DateTimeFormatter PUB_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

    /**
     * RSS URL로 HTTP 요청 후 XML을 파싱하여 Article 목록 반환
     */
    public List<Article> parse(CategoryType category) {
        List<Article> articles = new ArrayList<>();
        try {
            Document document = fetchDocument(category.getUrl());
            NodeList items = document.getElementsByTagName("item");

            for (int i = 0; i < items.getLength(); i++) {
                Article article = toArticle((Element) items.item(i), category);
                if (article != null) {
                    articles.add(article);
                }
            }
        } catch (Exception e) {
            log.error("RSS 파싱 실패 - category: {}, error: {}", category.name(), e.getMessage());
        }
        return articles;
    }

    /**
     * XML Document 가져오기
     */
    private Document fetchDocument(String url) throws Exception {
        InputStream inputStream = URI.create(url).toURL().openStream();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        return factory.newDocumentBuilder().parse(inputStream);
    }

    /**
     * XML item 엘리먼트를 Article 엔티티로 변환
     */
    private Article toArticle(Element item, CategoryType category) {
        try {
            String link = getTagValue(item, "link");
            String title = getTagValue(item, "title");
            String author = getTagValue(item, "dc:creator");
            String pubDateStr = getTagValue(item, "pubDate");

            if (link == null || title == null || author == null || pubDateStr == null) {
                log.warn("필수 필드 누락");
                return null;
            }

            return Article.of(
                    extractArticleId(link),
                    title,
                    category,
                    link,
                    author,
                    ZonedDateTime.parse(pubDateStr, PUB_DATE_FORMATTER).toLocalDateTime()
            );
        } catch (Exception e) {
            log.warn("Article 변환 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * link URL 마지막 경로에서 article_id 추출
     */
    private String extractArticleId(String link) {
        return link.substring(link.lastIndexOf("/") + 1);
    }

    /**
     * XML 엘리먼트에서 태그 값 추출
     */
    private String getTagValue(Element item, String tagName) {
        NodeList nodes = item.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            return null;
        }
        return nodes.item(0).getTextContent().trim();
    }
}