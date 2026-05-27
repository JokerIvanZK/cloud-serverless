package cn.ivanzk.bot.kook;

import cn.ivanzk.bot.BotStatus;
import cn.ivanzk.bot.MessageSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Kook 未启用时的空实现。
 */
@Component
@ConditionalOnProperty(prefix = "cloud-serverless.bots.kook", name = "enabled", havingValue = "false", matchIfMissing = true)
public class NoopKookMessageSender implements MessageSender {

    @Override
    public void sendMessage(String target, String content) {
        // 未启用时安全忽略。
    }

    public void sendChannelMessage(String channelName, String content, boolean sanitize) {
        // 未启用时安全忽略。
    }

    public void sendDirectMessage(String userId, String content) {
        // 未启用时安全忽略。
    }

    public BotStatus status() {
        return BotStatus.disabled();
    }
}
