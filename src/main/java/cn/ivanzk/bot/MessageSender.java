package cn.ivanzk.bot;

/**
 * 可发送文本消息的平台适配器。
 */
public interface MessageSender {

    void sendMessage(String target, String content);
}
