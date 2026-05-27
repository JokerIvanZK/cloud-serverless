package cn.ivanzk.bot.discord;

import cn.ivanzk.config.CloudServerlessProperties;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Discord 网关配置。
 */
@Configuration
@ConditionalOnProperty(prefix = "cloud-serverless.bots.discord", name = "enabled", havingValue = "true")
public class DiscordGatewayConfiguration {

    @Bean
    public DiscordClient discordClient(CloudServerlessProperties properties) {
        return DiscordClient.create(properties.getBots().getDiscord().getToken());
    }

    @Bean(destroyMethod = "logout")
    public GatewayDiscordClient gatewayDiscordClient(DiscordClient discordClient) {
        return discordClient.login().block();
    }
}
