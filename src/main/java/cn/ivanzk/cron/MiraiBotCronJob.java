package cn.ivanzk.cron;

import cn.ivanzk.config.mirai.MiraiBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Mirai机器人定时任务
 *
 * @author zk
 */
@Component
@AutoConfigureAfter(MiraiBot.class)
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
            miraiBot.isOnline();
            miraiBot.sendMessage("心跳");
        } catch (Exception e) {
            System.err.println("心跳任务失败，原因：" + e.getMessage());
        }
    }
}
