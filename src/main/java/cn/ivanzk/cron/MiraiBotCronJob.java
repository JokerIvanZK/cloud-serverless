package cn.ivanzk.cron;

import cn.ivanzk.config.mirai.MiraiBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Mirai机器人定时任务
 *
 * @author zk
 */
@Component
@ConditionalOnBean(MiraiBot.class)
public class MiraiBotCronJob {
    @Autowired
    private MiraiBot miraiBot;

    /**
     * 心跳任务
     * 每天17点执行
     */
    @Scheduled(cron = "0 0 17 * * ?")
    public void heartBeat() {
        try {
            miraiBot.noticeAdmin("心跳");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
