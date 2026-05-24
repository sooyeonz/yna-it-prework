let categories = [];
let categoryMap = {};
let currentPage = 0;
let isLoading = false;
let isLastPage = false;
let observer = null;

async function init() {
    try {
        categories = await api.fetchCategories();
        categoryMap = Object.fromEntries(categories.map(c => [c.key, c.name]));
        render.tabs(categories);
        await loadArticles();
        bindEvents();
    } catch (e) {
        render.error("데이터를 불러오지 못했습니다.");
        console.error(e);
    }
}

function resetState() {
    currentPage = 0;
    isLoading = false;
    isLastPage = false;
    if (observer) {
        observer.disconnect();
        observer = null;
    }
}

async function loadArticles(append = false) {
    if (isLoading || isLastPage) return;
    isLoading = true;

    if (!append) render.loading();

    try {
        const currentTab = tabState.currentTab;
        let articles, last;

        if (currentTab === "ALL") {
            const result = await api.fetchAllCategories(categories.map(c => c.key), currentPage);
            articles = result.articles;
            last = result.last;
        } else {
            const result = await api.fetchArticles(currentTab, currentPage);
            articles = result.content;
            last = result.last;
        }

        const enriched = articles.map(a => ({
            ...a,
            categoryName: categoryMap[a.category] ?? a.category,
        }));

        if (append) {
            render.appendArticles(enriched);
        } else {
            render.articleList(enriched);
        }

        currentPage++;
        isLastPage = last;

        if (isLastPage) {
            render.endOfList();
        } else {
            setupObserver();
        }
    } catch (e) {
        if (append) {
            isLastPage = true;
            render.endOfList();
        } else {
            render.error("기사를 불러오지 못했습니다.");
        }
        console.error(e);
    } finally {
        isLoading = false;
    }
}

function setupObserver() {
    if (observer) observer.disconnect();
    const sentinel = document.getElementById("scroll-sentinel");
    if (!sentinel) return;

    observer = new IntersectionObserver(entries => {
        if (entries[0].isIntersecting && !isLoading && !isLastPage) {
            loadArticles(true);
        }
    }, {rootMargin: "300px"});

    observer.observe(sentinel);
}

function bindEvents() {
    document.getElementById("tabs").addEventListener("click", async e => {
        const btn = e.target.closest("[data-tab-key]");
        if (!btn) return;
        tabState.setTab(btn.dataset.tabKey);
        render.tabs(categories);
        resetState();
        await loadArticles();
    });

    document.getElementById("article-list").addEventListener("click", async e => {
        const row = e.target.closest("[data-article-id]");
        if (!row) return;
        const {articleId, articleLink} = row.dataset;
        if (row.dataset.read !== "true") {
            row.dataset.read = "true";
            await api.setRead(articleId).catch(console.error);
        }
        if (articleLink) window.open(articleLink, "_blank", "noopener,noreferrer");
    });
}

init();
