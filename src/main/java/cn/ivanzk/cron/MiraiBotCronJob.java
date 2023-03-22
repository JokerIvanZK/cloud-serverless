package cn.ivanzk.cron;

import cn.ivanzk.config.mirai.MiraiBot;
import cn.ivanzk.config.mirai.MiraiBotProperties;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
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
    private Bot bot;
    @Autowired
    private MiraiBotProperties miraiBotProperties;
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

    /**
     * 重连
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void reConnect() {
        try {
            if (!bot.isOnline()) {
                bot = BotFactory.INSTANCE.newBot(miraiBotProperties.getQq(), miraiBotProperties.getPassword(), new BotConfiguration() {{
                    noNetworkLog();
                    fileBasedDeviceInfo(miraiBotProperties.getDeviceInfoPath());
                    setProtocol(miraiBotProperties.getProtocol());
                    autoReconnectOnForceOffline();
                }});
                bot.login();
                if (bot.isOnline()) {
                    bot.getFriend(miraiBotProperties.getAdmin()).sendMessage(String.format("QQ:%s<%s>重连", bot.getNick(), bot.getId()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
