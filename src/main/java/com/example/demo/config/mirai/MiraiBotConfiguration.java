package com.example.demo.config.mirai;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * QQ机器人配置
 *
 * @author zk
 */
@Configuration
@ConditionalOnProperty(name = "mirai.enable", havingValue = "true")
public class MiraiBotConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "mirai")
    public MiraiBotProperties miraiBotProperties() {
        return new MiraiBotProperties();
    }

    @Bean
    public Bot bot(MiraiBotProperties miraiBotProperties) {
        Bot bot = BotFactory.INSTANCE.newBot(miraiBotProperties.getQq(), miraiBotProperties.getPassword(), new BotConfiguration() {{
            fileBasedDeviceInfo(miraiBotProperties.getDeviceInfoPath());
            noNetworkLog();
            autoReconnectOnForceOffline();
            setProtocol(MiraiProtocol.IPAD);
        }});
        bot.login();
        if (bot.isOnline()) {
            bot.getFriend(miraiBotProperties.getAdmin()).sendMessage(String.format("QQ:%s<%s>上线", bot.getNick(), bot.getId()));
        }
        return bot;
    }
}
