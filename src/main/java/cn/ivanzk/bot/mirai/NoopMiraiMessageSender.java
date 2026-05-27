package cn.ivanzk.bot.mirai;

import cn.ivanzk.bot.BotStatus;
import cn.ivanzk.bot.MessageSender;

/**
 * Mirai 未启用时的空实现。
 */
public class NoopMiraiMessageSender implements MessageSender {

    @Override
    public void sendMessage(String target, String content) {
        // 未启用时安全忽略。
    }

    public void sendBroadcast(String content) {
        // 未启用时安全忽略。
    }

    public void noticeAdmin(String content) {
        // 未启用时安全忽略。
    }

    public BotStatus status() {
        return BotStatus.disabled();
    }
}
