package cn.ivanzk.config.mirai;

import net.mamoe.mirai.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.stereotype.Component;

/**
 * @author zk
 */
@Component
@AutoConfigureAfter(MiraiBotConfiguration.class)
public class MiraiBot {
    @Autowired
    private Bot bot;
    @Autowired
    private MiraiBotProperties miraiBotProperties;

    /**
     * 发送消息=>admin
     */
    public void noticeAdmin(String message) {
        if (!bot.isOnline()) {
            return;
        }
        bot.getFriend(miraiBotProperties.getAdmin()).sendMessage(message);
    }

    /**
     * 发送消息=>全部指定好友和群
     */
    public void sendMessage(String message) {
        if (!bot.isOnline()) {
            return;
        }
        for (Long aLong : miraiBotProperties.getFriends()) {
            bot.getFriend(aLong).sendMessage(message);
        }
        for (Long aLong : miraiBotProperties.getGroups()) {
            bot.getGroup(aLong).sendMessage(message);
        }
    }

    /**
     * 是否在线
     */
    public void isOnline() {
        if(!bot.isOnline()){
            bot.close();
            bot.login();
        }
    }
}
