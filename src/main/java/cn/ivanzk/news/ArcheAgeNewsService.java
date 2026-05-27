package cn.ivanzk.news;

import java.util.Collections;
import java.util.List;

import cn.ivanzk.bot.kook.KookMessageSender;
import cn.ivanzk.config.CloudServerlessProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * ArcheAge 公告推送服务。
 */
@Service
public class ArcheAgeNewsService {

    private static final Logger log = LoggerFactory.getLogger(ArcheAgeNewsService.class);

    private final CloudServerlessProperties properties;
    private final NewsCrawler newsCrawler;
    private final KookMessageSender kookMessageSender;
    private String lastPushUrl;

    public ArcheAgeNewsService(CloudServerlessProperties properties,
                               NewsCrawler newsCrawler,
                               ObjectProvider<KookMessageSender> kookMessageSender) {
        this.properties = properties;
        this.newsCrawler = newsCrawler;
        this.kookMessageSender = kookMessageSender.getIfAvailable();
        this.lastPushUrl = properties.getNews().getArcheage().getLastPushUrl();
    }

    public void pushUpdates() {
        CloudServerlessProperties.ArcheAge archeAge = properties.getNews().getArcheage();
        if (!archeAge.isEnabled()) {
            return;
        }
        if (kookMessageSender == null) {
            log.debug("Skip ArcheAge news push because Kook bot is disabled");
            return;
        }

        List<String> updates = findPendingUrls(archeAge);
        for (String newsUrl : updates) {
            String fullUrl = archeAge.getBaseUrl() + newsUrl;
            String title = newsCrawler.readTitle(fullUrl);
            String message = StringUtils.hasText(title) ? title + "\r\n" + fullUrl : fullUrl;
            kookMessageSender.sendChannelMessage(archeAge.getTargetChannel(), message, false);
            lastPushUrl = newsUrl;
            log.info("Pushed ArcheAge news: {}", newsUrl);
        }
    }

    List<String> findPendingUrls(CloudServerlessProperties.ArcheAge archeAge) {
        List<String> newsUrls = newsCrawler.listNewsUrls(archeAge.getBaseUrl() + archeAge.getListPath());
        if (newsUrls.isEmpty()) {
            return Collections.emptyList();
        }
        if (!StringUtils.hasText(lastPushUrl)) {
            lastPushUrl = newsUrls.get(newsUrls.size() - 1);
            return Collections.emptyList();
        }
        int lastIndex = newsUrls.indexOf(lastPushUrl);
        if (lastIndex < 0) {
            return newsUrls;
        }
        return newsUrls.subList(lastIndex + 1, newsUrls.size());
    }
}
