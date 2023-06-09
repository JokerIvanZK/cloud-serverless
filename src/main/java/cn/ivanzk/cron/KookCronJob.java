package cn.ivanzk.cron;

import cn.ivanzk.config.kook.KookClient;
import cn.ivanzk.config.kook.KookProperties;
import cn.ivanzk.util.HtmlParseUtil;
import com.java.comn.util.SmallTool;
import com.net.comn.server.ServerContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mirai机器人定时任务
 *
 * @author zk
 */
@Component
@ConditionalOnBean(KookClient.class)
public class KookCronJob {
    @Value("${lastPushUrl}")
    private String lastPushUrl = null;

    /**
     * 心跳任务
     * 每天17点执行
     */
    @Scheduled(cron = "0 0 17 * * ?")
    public void heartBeat() {
        KookClient kookClient = ServerContext.getBean(KookClient.class);
        KookProperties kookProperties = ServerContext.getBean(KookProperties.class);
        if (kookClient == null || kookProperties == null) {
            return;
        }
        try {
            kookClient.directMessage(kookProperties.getAdmin(), "心跳");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询更新
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void updateMessage() {
        KookClient kookClient = ServerContext.getBean(KookClient.class);
        KookProperties kookProperties = ServerContext.getBean(KookProperties.class);
        if (kookClient == null || kookProperties == null) {
            return;
        }

        List<String> awaitPushUrl = HtmlParseUtil.doGetForRegexStringList("https://asia.archeage.com/news?lang=zh_TW", "/news/{1}[\\d]+[?]{1}page={1}[\\d]+");
        if (SmallTool.isEmpty(lastPushUrl)) {
            lastPushUrl = awaitPushUrl.get(awaitPushUrl.size() - 1);
        }
        awaitPushUrl = awaitPushUrl.subList(awaitPushUrl.indexOf(lastPushUrl) + 1, awaitPushUrl.size());

        for (String newsUrl : awaitPushUrl) {
            String url = "https://asia.archeage.com" + newsUrl;
            String title = HtmlParseUtil.doGetForRegexString(url, "<h3>{1}[\\d\\D]+<em class=\"notice-id\">{1}");

            if (SmallTool.notEmpty(title)) {
                title = title.substring(title.indexOf(">") + 1, title.lastIndexOf("<"));
                url = title + "\r\n" + url;
            }
            kookClient.channelMessage("更新", url, false);
            lastPushUrl = newsUrl;
        }
    }
}