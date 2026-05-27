package cn.ivanzk.news;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NewsCrawlerTest {

    private final NewsCrawler crawler = new NewsCrawler(null);

    @Test
    void shouldParseNewsUrlsInPushOrder() {
        String html = """
                <a href="/news/4740?page=1">old</a>
                <a href="/news/4741?page=1">new</a>
                """;

        assertThat(crawler.listNewsUrlsFromHtml(html))
                .containsExactly("/news/4741?page=1", "/news/4740?page=1");
    }

    @Test
    void shouldParseTitleWithoutHtmlTags() {
        String html = "<h3>更新公告 <span>Alpha</span><em class=\"notice-id\">#1</em>";

        assertThat(crawler.readTitleFromHtml(html)).isEqualTo("更新公告 Alpha");
    }

    @Test
    void shouldReturnEmptyTitleWhenTitleIsMissing() {
        assertThat(crawler.readTitleFromHtml("<html></html>")).isEmpty();
    }
}
