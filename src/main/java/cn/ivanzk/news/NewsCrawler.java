package cn.ivanzk.news;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * ArcheAge 公告页面抓取。
 */
@Component
public class NewsCrawler {

    private static final Logger log = LoggerFactory.getLogger(NewsCrawler.class);
    private static final Pattern NEWS_URL_PATTERN = Pattern.compile("/news/\\d+\\?page=\\d+");
    private static final Pattern TITLE_PATTERN = Pattern.compile("<h3>([\\d\\D]+?)<em class=\"notice-id\">");
    private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]+>");

    private final RestClient restClient;

    public NewsCrawler() {
        this(RestClient.builder().build());
    }

    NewsCrawler(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<String> listNewsUrls(String url) {
        String html = get(url);
        if (html.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> urls = new ArrayList<>();
        Matcher matcher = NEWS_URL_PATTERN.matcher(html);
        while (matcher.find()) {
            urls.add(0, matcher.group());
        }
        return urls;
    }

    public String readTitle(String url) {
        String html = get(url);
        if (html.isEmpty()) {
            return "";
        }
        Matcher matcher = TITLE_PATTERN.matcher(html);
        if (!matcher.find()) {
            return "";
        }
        return TAG_PATTERN.matcher(matcher.group(1)).replaceAll("").trim();
    }

    List<String> listNewsUrlsFromHtml(String html) {
        List<String> urls = new ArrayList<>();
        Matcher matcher = NEWS_URL_PATTERN.matcher(html);
        while (matcher.find()) {
            urls.add(0, matcher.group());
        }
        return urls;
    }

    String readTitleFromHtml(String html) {
        Matcher matcher = TITLE_PATTERN.matcher(html);
        if (!matcher.find()) {
            return "";
        }
        return TAG_PATTERN.matcher(matcher.group(1)).replaceAll("").trim();
    }

    private String get(String url) {
        try {
            String html = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(String.class);
            return html == null ? "" : html;
        } catch (Exception e) {
            log.warn("Fetch news page failed: url={}, error={}", url, e.getMessage());
            return "";
        }
    }
}
