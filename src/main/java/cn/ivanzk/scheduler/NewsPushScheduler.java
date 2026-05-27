package cn.ivanzk.scheduler;

import cn.ivanzk.news.ArcheAgeNewsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 公告推送调度。
 */
@Component
public class NewsPushScheduler {

    private final ArcheAgeNewsService newsService;

    public NewsPushScheduler(ArcheAgeNewsService newsService) {
        this.newsService = newsService;
    }

    @Scheduled(cron = "${cloud-serverless.schedule.news-cron}")
    public void pushNews() {
        newsService.pushUpdates();
    }
}
