package cn.ivanzk.bot.mirai;

import java.util.List;

import cn.ivanzk.bot.BotStatus;
import cn.ivanzk.bot.MessageSender;
import cn.ivanzk.bot.MessageTextSanitizer;
import cn.ivanzk.config.CloudServerlessProperties;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mirai QQ 消息发送适配器。
 */
public class MiraiMessageSender implements MessageSender {

    private static final Logger log = LoggerFactory.getLogger(MiraiMessageSender.class);

    private final CloudServerlessProperties properties;
    private final MessageTextSanitizer sanitizer;
    private final Bot bot;

    public MiraiMessageSender(CloudServerlessProperties properties, MessageTextSanitizer sanitizer, Bot bot) {
        this.properties = properties;
        this.sanitizer = sanitizer;
        this.bot = bot;
    }

    @Override
    public void sendMessage(String target, String content) {
        sendBroadcast(content);
    }

    public void sendBroadcast(String content) {
        if (!isOnline()) {
            log.debug("Skip Mirai broadcast because bot is offline");
            return;
        }
        String message = sanitizer.forMirai(content);
        sendFriends(properties.getBots().getMirai().getFriends(), message);
        sendGroups(properties.getBots().getMirai().getGroups(), message);
    }

    public void noticeAdmin(String content) {
        if (!isOnline()) {
            log.debug("Skip Mirai admin notice because bot is offline");
            return;
        }
        Long admin = properties.getBots().getMirai().getAdmin();
        if (admin == null) {
            log.debug("Skip Mirai admin notice because admin is not configured");
            return;
        }
        Friend friend = bot.getFriend(admin);
        if (friend == null) {
            log.warn("Mirai admin friend {} is not available", admin);
            return;
        }
        friend.sendMessage(content);
    }

    public BotStatus status() {
        if (!properties.getBots().getMirai().isEnabled()) {
            return BotStatus.disabled();
        }
        return isOnline() ? BotStatus.onlineStatus() : BotStatus.offline("bot is offline");
    }

    private boolean isOnline() {
        return bot != null && bot.isOnline();
    }

    private void sendFriends(List<Long> friends, String message) {
        for (Long friendId : friends) {
            Friend friend = bot.getFriend(friendId);
            if (friend == null) {
                log.warn("Mirai friend {} is not available", friendId);
                continue;
            }
            friend.sendMessage(message);
        }
    }

    private void sendGroups(List<Long> groups, String message) {
        for (Long groupId : groups) {
            Group group = bot.getGroup(groupId);
            if (group == null) {
                log.warn("Mirai group {} is not available", groupId);
                continue;
            }
            group.sendMessage(message);
        }
    }
}
