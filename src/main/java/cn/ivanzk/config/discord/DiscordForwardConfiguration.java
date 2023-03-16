package cn.ivanzk.config.discord;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zk
 */
@Configuration
@ConditionalOnProperty(name = "service.discord-forward.enable", havingValue = "true")
public class DiscordForwardConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "service.discord-forward.forward")
    public DiscordForwardProperties discordForwardProperties() {
        return new DiscordForwardProperties();
    }
}
