package cn.ivanzk.scheduler;

import cn.ivanzk.bot.kook.KookMessageSender;
import cn.ivanzk.bot.mirai.MiraiMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 机器人心跳调度。
 */
@Component
public class BotHeartbeatScheduler {

    private static final Logger log = LoggerFactory.getLogger(BotHeartbeatScheduler.class);

    private final MiraiMessageSender miraiMessageSender;
    private final KookMessageSender kookMessageSender;

    public BotHeartbeatScheduler(ObjectProvider<MiraiMessageSender> miraiMessageSender,
                                 ObjectProvider<KookMessageSender> kookMessageSender) {
        this.miraiMessageSender = miraiMessageSender.getIfAvailable();
        this.kookMessageSender = kookMessageSender.getIfAvailable();
    }

    @Scheduled(cron = "${cloud-serverless.schedule.heartbeat-cron}")
    public void heartbeat() {
        if (miraiMessageSender != null) {
            miraiMessageSender.noticeAdmin("心跳");
        }
        if (kookMessageSender != null) {
            kookMessageSender.sendDirectMessage(null, "心跳");
        }
        log.debug("Heartbeat scheduler completed");
    }
}
