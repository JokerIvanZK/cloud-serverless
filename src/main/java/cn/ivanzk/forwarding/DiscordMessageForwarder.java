package cn.ivanzk.forwarding;

import cn.ivanzk.bot.kook.KookMessageSender;
import cn.ivanzk.bot.mirai.MiraiMessageSender;
import cn.ivanzk.bot.mirai.NoopMiraiMessageSender;
import cn.ivanzk.config.CloudServerlessProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Discord 消息转发编排。
 */
@Service
public class DiscordMessageForwarder {

    private static final Logger log = LoggerFactory.getLogger(DiscordMessageForwarder.class);

    private final CloudServerlessProperties properties;
    private final MiraiMessageSender miraiSender;
    private final KookMessageSender kookSender;

    public DiscordMessageForwarder(CloudServerlessProperties properties,
                                   org.springframework.beans.factory.ObjectProvider<MiraiMessageSender> miraiSender,
                                   org.springframework.beans.factory.ObjectProvider<KookMessageSender> kookSender) {
        this.properties = properties;
        this.miraiSender = miraiSender.getIfAvailable();
        this.kookSender = kookSender.getIfAvailable();
    }

    public void forward(String channelName, String content) {
        if (!StringUtils.hasText(channelName) || !StringUtils.hasText(content)) {
            return;
        }
        if (properties.getForwarding().getDiscordToMirai().isEnabled()
                && matches(properties.getForwarding().getDiscordToMirai(), channelName)
                && miraiSender != null) {
            miraiSender.sendBroadcast(content);
        }
        if (properties.getForwarding().getDiscordToKook().isEnabled()
                && matches(properties.getForwarding().getDiscordToKook(), channelName)
                && kookSender != null) {
            kookSender.sendChannelMessage(channelName, content, true);
        }
        log.debug("Discord message processed: channel={}", channelName);
    }

    private boolean matches(CloudServerlessProperties.ForwardRule rule, String channelName) {
        return rule.getChannelNames().stream()
                .filter(StringUtils::hasText)
                .anyMatch(channelName::contains);
    }
}
