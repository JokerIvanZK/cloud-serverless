package cn.ivanzk.bot.discord;

import cn.ivanzk.bot.BotStatus;
import cn.ivanzk.config.CloudServerlessProperties;
import discord4j.core.GatewayDiscordClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Discord 运行状态。
 */
@Component
public class DiscordBotStatusProvider {

    private final CloudServerlessProperties properties;
    private final GatewayDiscordClient gatewayDiscordClient;

    public DiscordBotStatusProvider(CloudServerlessProperties properties,
                                    org.springframework.beans.factory.ObjectProvider<GatewayDiscordClient> gatewayDiscordClient) {
        this.properties = properties;
        this.gatewayDiscordClient = gatewayDiscordClient.getIfAvailable();
    }

    public BotStatus status() {
        if (!properties.getBots().getDiscord().isEnabled()) {
            return BotStatus.disabled();
        }
        if (!StringUtils.hasText(properties.getBots().getDiscord().getToken())) {
            return BotStatus.unavailable("token is empty");
        }
        return gatewayDiscordClient == null ? BotStatus.offline("gateway is not connected") : BotStatus.onlineStatus();
    }
}
