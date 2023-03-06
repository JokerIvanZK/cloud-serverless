package cn.ivanzk.config.discord;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * discord配置
 *
 * @author zk
 */
@Configuration
@ConditionalOnProperty(name = "discord.enable", havingValue = "true")
public class DiscordConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "discord")
    public DiscordProperties discordProperties() {
        return new DiscordProperties();
    }

    @Bean
    public DiscordClient discordClient(DiscordProperties discordProperties) {
        return DiscordClient.create(discordProperties.getToken());
    }

    @Bean
    public GatewayDiscordClient gatewayDiscordClient(DiscordClient discordClient) {
        return discordClient.login().block();
    }
}
