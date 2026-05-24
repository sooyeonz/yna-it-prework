const BASE_URL = "/api";

const api = {
    async fetchCategories() {
        const res = await fetch(`${BASE_URL}/categories`);
        if (!res.ok) throw new Error("카테고리 로드 실패");
        return res.json(); // CategoryResponse[]
    },

    async fetchArticles(categoryKey, page = 0, size = 20) {
        const res = await fetch(`${BASE_URL}/articles?category=${categoryKey}&page=${page}&size=${size}`);
        if (!res.ok) throw new Error("기사 로드 실패");
        return res.json(); // Page<ArticleResponse>
    },

    async fetchAllCategories(categoryKeys, page = 0) {
        const results = await Promise.all(
            categoryKeys.map(key => this.fetchArticles(key, page))
        );
        const all = results.flatMap((r, i) =>
            (r.content || []).map(a => ({...a, category: categoryKeys[i]}))
        );
        all.sort((a, b) => new Date(b.pubDate) - new Date(a.pubDate));
        return {
            articles: all,
            last: results.every(r => r.last),
        };
    },

    async setRead(articleId) {
        const res = await fetch(`${BASE_URL}/articles/${articleId}/read`, {method: "PATCH"});
        if (!res.ok) throw new Error("읽음 처리 실패");
    },
};

