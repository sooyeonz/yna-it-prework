# 뉴스 열람 웹 애플리케이션 · 백엔드 푸시 시스템

연합뉴스 RSS 피드를 수집·저장하고, 카테고리별로 기사를 열람할 수 있는 웹 애플리케이션과 개인화 푸시 알림 백엔드를 Spring Boot 기반으로 통합 구현한 프로젝트입니다.

---

## 주요 기능

**웹 애플리케이션**

- 카테고리 탭: 전체 / 정치 / 북한 / 경제 / 산업 / 사회 탭 전환
- 기사 정렬: `pubDate` 최신순 표시
- 무한 스크롤: IntersectionObserver로 스크롤 끝에서 다음 페이지 자동 로드
- 읽음 상태: 기사 클릭 시 서버 DB 저장 (`PATCH /api/articles/{articleId}/read`)
- 읽음 시각화: 기사 제목, 관련 데이터 흐리게

**푸시 알림 백엔드**

- RSS 수집: 10분 주기로 5개 카테고리 RSS 피드 수집
- 중복 제거: article_id 기준 신규 기사만 저장, 최대 1,000건 유지
- 유저 매칭: 선호 카테고리 기반 발송 대상 필터링
- DND 처리: 방해 금지 시간대 유저 발송 제외
- 푸시 발송: push_type(APNS/FCM)에 따라 분기 발송
- 이력 저장: 발송 결과(success/fail) DB 일괄 저장

---

## API 명세

| 메서드     | 경로                                        | 설명                           |
|---------|-------------------------------------------|------------------------------|
| `GET`   | `/api/categories`                         | 카테고리 목록 반환 (`[{key, name}]`) |
| `GET`   | `/api/articles?category=X&page=0&size=20` | 카테고리별 기사 목록 (페이지네이션, 최신순)    |
| `PATCH` | `/api/articles/{articleId}/read`          | 기사 읽음 처리                     |

---

## 화면 구성

**전체 탭**

- 전 카테고리 기사를 `pubDate` 최신순으로 표시
- 카테고리별 API를 병렬 호출 후 클라이언트에서 병합·정렬
- 각 기사에 카테고리별(정치·북한·경제·산업·사회) 색상 라벨 표시

**각 카테고리 탭**

- 탭 클릭 시 해당 카테고리로 필터링
- 호버 시 글씨 파란색 하이라이트
- 선택된 탭은 파란색 배경으로 강조

**읽음/미읽음 구분**

- 호버 시 배경 하이라이트
- 기사 클릭 즉시 읽음 상태로 전환
- 읽은 기사는 기사 제목, 관련 데이터 회색으로 변경

**무한 스크롤**

- 스크롤이 목록 끝에 가까워지면 IntersectionObserver가 다음 페이지를 자동 로드

**기사 본문 새 탭으로 열기**

- 기사 클릭 시 원문 URL을 새 탭(`window.open`)으로 열기
- 목록 탭에서는 읽음 상태가 즉시 반영

---

## 기술 스택

| 항목                           | 버전       |
|------------------------------|----------|
| Java                         | 17       |
| Spring Boot                  | 3.5.14   |
| Spring Data JPA              | -        |
| Hibernate Community Dialects | -        |
| SQLite JDBC                  | 3.49.1.0 |
| Lombok                       | -        |
| Tailwind CSS (CDN)           | -        |
| Pretendard (CDN)             | -        |

---

## 실행 방법

**사전 조건:** Java 17 이상

**macOS / Linux**

```bash
./gradlew bootRun
```

**Windows**

```bash
gradlew.bat bootRun
```

- 실행 후 브라우저에서 `http://localhost:8080` 접속.
- `data.sql`의 사용자 데이터가 자동으로 초기 적재되고, 10분 주기로 RSS 수집 및 푸시 발송이 시작됩니다.

---

## 전체 처리 흐름

```
[브라우저]
  ├─ GET /api/categories         → 카테고리 탭 렌더링
  ├─ GET /api/articles?category  → 기사 리스트 (무한 스크롤)
  └─ PATCH /api/articles/{id}/read → 읽음 처리

[ArticlesScheduler] (10분 주기)
  ├─ RssCollector.collectAll()
  │     └─ RssParser.parse(category)
  ├─ ArticleService.saveNewArticles()
  │     ├─ 중복 기사 필터링 (article_id 기준)
  │     ├─ 신규 기사 저장
  │     └─ 1,000건 초과 시 오래된 기사 삭제
  └─ PushService.sendPushNotifications()
        ├─ 카테고리별 구독 유저 조회
        ├─ DND 시간대 유저 제외
        ├─ push_type에 따라 APNS / FCM 발송
        └─ 발송 이력 일괄 저장
```

---

## 프로젝트 구조

```
src/main/java/com/yna/itprework/
├── article/
│   ├── CategoryType.java
│   ├── controller/
│   │   ├── ArticleController.java
│   │   └── dto/                       # ArticleResponse, CategoryResponse
│   ├── entity/Article.java
│   ├── repository/ArticleRepository.java
│   └── service/ArticleService.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ErrorResponse.java
│   └── InvalidCategoryException.java
├── notification/
│   ├── entity/NotificationLog.java
│   ├── repository/NotificationLogRepository.java
│   └── service/
│       ├── PushNotificationService.java
│       ├── PushNotificationServiceImpl.java  # 구현체 (success/fail 랜덤)
│       └── PushService.java                  # 발송 오케스트레이션
├── rss/
│   ├── RssCollector.java
│   └── RssParser.java
├── scheduler/
│   └── ArticlesScheduler.java
└── user/
    ├── PushType.java
    ├── entity/User.java               # DND 로직 포함
    ├── entity/UserCategory.java
    └── repository/UserRepository.java

src/main/resources/static/
├── index.html
└── js/
    ├── api.js
    ├── app.js       # 상태 관리 + 이벤트
    ├── render.js
    └── tabState.js
```

---

## 데이터 모델

### articles

| 컬럼         | 타입           | 설명                                                    |
|------------|--------------|-------------------------------------------------------|
| article_id | VARCHAR (PK) | 기사 고유 ID (link URL 마지막 경로 추출)                         |
| title      | VARCHAR      | 기사 제목                                                 |
| category   | VARCHAR      | POLITICS / NORTH_KOREA / ECONOMY / INDUSTRY / SOCIETY |
| link       | VARCHAR      | 기사 원문 URL                                             |
| author     | VARCHAR      | 기사 작성자 (dc:creator)                                   |
| pub_date   | DATETIME     | 기사 발행 시각                                              |
| is_read    | BOOLEAN      | 읽음 여부 (기본값 false)                                     |

### users

| 컬럼        | 타입               | 설명                                      |
|-----------|------------------|-----------------------------------------|
| id        | INTEGER (PK)     | 사용자 번호                                  |
| name      | VARCHAR          | 사용자 이름                                  |
| device_id | VARCHAR (UNIQUE) | 기기 고유 ID                                |
| push_type | VARCHAR          | FCM 또는 APNS                             |
| dnd_time  | VARCHAR          | 방해 금지 시간대 (예: `23:00-11:00`, 미설정 시 `-`) |

### user_categories

| 컬럼       | 타입                   | 설명      |
|----------|----------------------|---------|
| id       | INTEGER (PK)         | -       |
| user_id  | INTEGER (FK → users) | 사용자 ID  |
| category | VARCHAR              | 구독 카테고리 |

### notification_logs

| 컬럼               | 타입           | 설명                         |
|------------------|--------------|----------------------------|
| id               | INTEGER (PK) | -                          |
| device_id        | VARCHAR      | 발송 대상 기기 ID                |
| push_type        | VARCHAR      | FCM 또는 APNS                |
| article_id       | VARCHAR      | 발송된 기사 ID                  |
| article_title    | VARCHAR      | 발송된 기사 제목                  |
| article_category | VARCHAR      | 기사 카테고리                    |
| status           | VARCHAR      | 발송 결과 (`success` / `fail`) |
| created_at       | DATETIME     | 발송 시각                      |

---

## SQLite DB 확인 방법

**DB 파일 경로**

- 프로젝트 루트 디렉토리에 `app.db` 생성 (`bootRun` 실행 기준)

```bash
# DB 접속 (프로젝트 루트에서 실행)
sqlite3 app.db

# 테이블 목록 확인
.tables
# articles  notification_logs  user_categories  users

# 테이블 스키마 확인
.schema articles
.schema notification_logs
.schema users
.schema user_categories
```

### 조회 예시

**기사 조회**

```sql
-- 수집된 기사 최신순 조회
SELECT article_id, title, category, pub_date
FROM articles
ORDER BY pub_date DESC LIMIT 10;

-- 카테고리별 기사 수 집계
SELECT category, COUNT(*) AS cnt
FROM articles
GROUP BY category;
```

**알림 발송 이력 조회**

```sql
-- 최근 발송 이력 조회
SELECT id, device_id, push_type, article_title, article_category, status, created_at
FROM notification_logs
ORDER BY created_at DESC LIMIT 20;

-- 카테고리별 발송 성공/실패 집계
SELECT article_category, status, COUNT(*) AS cnt
FROM notification_logs
GROUP BY article_category, status;
```

**유저 데이터 조회**

```sql
-- 전체 유저 목록 (DND 시간대 포함)
SELECT id, name, push_type, dnd_time
FROM users;

-- 유저별 구독 카테고리 조회
SELECT u.name, uc.category
FROM users u
         JOIN user_categories uc ON u.id = uc.user_id
ORDER BY u.id;
```

```bash
# 종료
.quit
```

- [DB Browser for SQLite](https://sqlitebrowser.org/) 등 GUI 툴에서 `app.db` 파일을 직접 열어 조회할 수도 있습니다.

---

## 주요 설정 (`application.yaml`)

| 항목                      | 기본값                  | 설명             |
|-------------------------|----------------------|----------------|
| `rss.schedule-rate`     | `600000` (10분)       | RSS 수집 주기 (ms) |
| `rss.max-article-count` | `1000`               | 최대 기사 저장 수     |
| `spring.datasource.url` | `jdbc:sqlite:app.db` | SQLite DB 경로   |

---

## 구현 포인트

**무한 스크롤 (IntersectionObserver)**

- 목록 끝에 sentinel `<div>`를 삽입하고 `IntersectionObserver`로 감지해 다음 페이지를 자동 로드
- 탭 전환 시 옵저버를 즉시 해제하고 첫 로드 완료 후 재설정해 중복 요청을 방지

**낙관적 읽음 처리**

- 기사 클릭 시 서버 응답을 기다리지 않고 DOM을 즉시 전환
- 이미 읽은 기사는 서버 요청 자체를 생략

**XSS 방어**

- RSS 피드에서 수집한 외부 데이터를 `innerHTML`에 삽입, `_escape()` 헬퍼로 특수문자를 HTML 엔티티로 치환

**전체(ALL) 탭**

- 백엔드 API 변경 없이 5개 카테고리 API를 `Promise.all`로 병렬 호출한 뒤 병합·정렬해 구현

**N+1 방지 및 배치 처리**

- `findExistingIds()` JPQL로 기존 기사를 한 번에 조회
- `saveAll()`로 신규 기사 및 발송 이력 일괄 저장
- `JOIN FETCH`로 UserCategory N+1 방지

**전역 REST API 예외 처리**

- `@RestControllerAdvice`로 예외를 일괄 처리하고 모든 오류 응답을 `{"message": "..."}` 형태로 통일

---

## AI 활용 내역

Claude와 Claude Code를 활용해 프로젝트 개발을 진행했습니다.

### 활용 내용

**코드 작성**

- 클래스와 전체 아키텍처 직접 설계한 후 AI는 구현을 보조하는 역할로 활용

**리뷰 및 리팩토링**

- 백엔드 전체 코드에서 SOLID 원칙 위반, N+1 쿼리 문제, 트랜잭션 범위 오류를 분석하도록 요청
- 프론트엔드 코드에서 버그 탐지 및 수정

**적용 기준**

AI 제안을 그대로 수용하지 않고 제안의 타당성을 직접 검토한 후 수정 여부를 결정했습니다. 
