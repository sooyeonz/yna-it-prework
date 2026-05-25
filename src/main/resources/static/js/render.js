const CATEGORY_STYLE = {
    POLITICS: {text: "text-cat-POL", bg: "bg-cat-POL-bg"},
    NORTH_KOREA: {text: "text-cat-NK", bg: "bg-cat-NK-bg"},
    ECONOMY: {text: "text-cat-ECON", bg: "bg-cat-ECON-bg"},
    INDUSTRY: {text: "text-cat-IND", bg: "bg-cat-IND-bg"},
    SOCIETY: {text: "text-cat-SOC", bg: "bg-cat-SOC-bg"},
};

const render = {
    tabs(categories) {
        const allTab = {key: "ALL", name: "전체"};
        const tabs = [allTab, ...categories];
        const current = tabState.currentTab;

        document.getElementById("tabs").innerHTML = tabs.map(tab => `
      <button
        data-tab-key="${tab.key}"
        class="flex-1 py-3.5 whitespace-nowrap text-lg transition-colors
          ${tab.key === current
            ? "bg-accent text-white font-bold"
            : "bg-surface text-ink-2 hover:text-accent font-medium"}"
      >${tab.name}</button>
    `).join("");
    },

    articleList(articles) {
        const el = document.getElementById("article-list");

        if (articles.length === 0) {
            el.innerHTML = `
        <p class="py-20 text-center text-sm text-ink-3">기사가 없습니다.</p>
      `;
            return;
        }

        el.innerHTML = articles.map(_articleRow).join("");
        el.insertAdjacentHTML("beforeend", `<div id="scroll-sentinel"></div>`);
    },

    appendArticles(articles) {
        const el = document.getElementById("article-list");
        const sentinel = document.getElementById("scroll-sentinel");
        const html = articles.map(_articleRow).join("");

        if (sentinel) {
            sentinel.insertAdjacentHTML("beforebegin", html);
        } else {
            el.insertAdjacentHTML("beforeend", html);
        }
    },

    endOfList() {
        document.getElementById("scroll-sentinel")?.remove();
    },

    loading() {
        document.getElementById("article-list").innerHTML = `
      <p class="py-20 text-center text-sm text-ink-3">불러오는 중...</p>
    `;
    },

    error(message) {
        document.getElementById("article-list").innerHTML = `
      <p class="py-20 text-center text-sm text-ink-3">${message}</p>
    `;
    },
};

function _articleRow(article) {
    const isAllTab = tabState.currentTab === "ALL";

    return `
    <div
      data-article-id="${article.articleId}"
      data-article-link="${article.link}"
      data-read="${article.isRead}"
      class="group px-2 py-5 border-b border-line cursor-pointer transition-colors hover:bg-hover-bg"
    >
      ${isAllTab && article.category ? _chip(article.category, article.categoryName) : ""}
      <p class="text-lg font-bold leading-[1.35] tracking-[-0.015em] text-ink text-pretty mb-2 group-data-[read=true]:text-read group-data-[read=true]:font-medium">
        ${_escape(article.title)}
      </p>
      <p class="text-xs text-ink-3 group-data-[read=true]:text-read">
        ${_escape(article.author)} 기자 · ${_formatDate(article.pubDate)}
      </p>
    </div>
  `;
}

function _escape(str) {
    return String(str)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;");
}

function _chip(categoryKey, categoryName) {
    const style = CATEGORY_STYLE[categoryKey] || {};
    return `
    <div class="inline-flex items-center gap-1.5 px-2 py-0.5 rounded text-xs font-bold tracking-[0.04em] w-fit mb-2 ${style.text} ${style.bg} group-data-[read=true]:!text-read group-data-[read=true]:!bg-read-bg">
      ${categoryName}
    </div>
  `;
}

function _formatDate(pubDate) {
    if (!pubDate) return "";
    const d = new Date(pubDate);
    const month = d.getMonth() + 1;
    const day = d.getDate();
    const hour = String(d.getHours()).padStart(2, "0");
    const min = String(d.getMinutes()).padStart(2, "0");
    return `${month}/${day} ${hour}:${min}`;
}
