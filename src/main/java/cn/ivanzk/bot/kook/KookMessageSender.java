package cn.ivanzk.bot.kook;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.ivanzk.bot.BotStatus;
import cn.ivanzk.bot.MessageSender;
import cn.ivanzk.bot.MessageTextSanitizer;
import cn.ivanzk.config.CloudServerlessProperties;
import com.github.houbb.opencc4j.util.ZhTwConverterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Kook 消息发送适配器。
 */
@Component
@ConditionalOnProperty(prefix = "cloud-serverless.bots.kook", name = "enabled", havingValue = "true")
public class KookMessageSender implements MessageSender {

    private static final Logger log = LoggerFactory.getLogger(KookMessageSender.class);

    private final CloudServerlessProperties properties;
    private final KookApiClient apiClient;
    private final MessageTextSanitizer sanitizer;
    private final List<KookChannel> channelCache = new CopyOnWriteArrayList<>();

    public KookMessageSender(CloudServerlessProperties properties, KookApiClient apiClient, MessageTextSanitizer sanitizer) {
        this.properties = properties;
        this.apiClient = apiClient;
        this.sanitizer = sanitizer;
    }

    @Override
    public void sendMessage(String target, String content) {
        sendChannelMessage(target, content, true);
    }

    public void sendChannelMessage(String channelName, String content, boolean sanitize) {
        if (!StringUtils.hasText(channelName) || !StringUtils.hasText(content)) {
            return;
        }
        String message = sanitize ? sanitizer.forKook(content) : content;
        for (KookChannel channel : channels()) {
            if (matches(channel.getName(), channelName)) {
                apiClient.sendChannelMessage(channel.getId(), message);
            }
        }
    }

    public void sendDirectMessage(String userId, String content) {
        String targetUserId = StringUtils.hasText(userId) ? userId : properties.getBots().getKook().getAdmin();
        if (!StringUtils.hasText(targetUserId)) {
            log.debug("Skip Kook direct message because target user is not configured");
            return;
        }
        apiClient.sendDirectMessage(targetUserId, sanitizer.forKook(content));
    }

    public BotStatus status() {
        if (!properties.getBots().getKook().isEnabled()) {
            return BotStatus.disabled();
        }
        if (!StringUtils.hasText(properties.getBots().getKook().getToken())) {
            return BotStatus.unavailable("token is empty");
        }
        return BotStatus.onlineStatus();
    }

    private List<KookChannel> channels() {
        if (channelCache.isEmpty()) {
            refreshChannels();
        }
        return channelCache;
    }

    private void refreshChannels() {
        List<KookChannel> channels = apiClient.listGuilds().stream()
                .flatMap(guild -> apiClient.listChannels(guild.getId()).stream())
                .toList();
        channelCache.clear();
        channelCache.addAll(channels);
    }

    private boolean matches(String actualName, String expectedName) {
        if (!StringUtils.hasText(actualName)) {
            return false;
        }
        return actualName.contains(expectedName) || actualName.contains(ZhTwConverterUtil.toTraditional(expectedName));
    }
}
