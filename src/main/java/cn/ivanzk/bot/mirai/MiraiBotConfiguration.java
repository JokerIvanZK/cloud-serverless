package cn.ivanzk.bot.mirai;

import cn.ivanzk.bot.MessageTextSanitizer;
import cn.ivanzk.config.CloudServerlessProperties;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.auth.BotAuthorization;
import net.mamoe.mirai.utils.BotConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * Mirai QQ 机器人配置。
 */
@Configuration
public class MiraiBotConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MiraiBotConfiguration.class);

    @Bean(destroyMethod = "close")
    @ConditionalOnProperty(prefix = "cloud-serverless.bots.mirai", name = "enabled", havingValue = "true")
    public Bot miraiBot(CloudServerlessProperties properties) {
        CloudServerlessProperties.Mirai mirai = properties.getBots().getMirai();
        Bot bot = BotFactory.INSTANCE.newBot(mirai.getQq(), BotAuthorization.byQRCode(), botConfiguration(mirai));
        bot.login();
        log.info("Mirai bot logged in: id={}, nick={}", bot.getId(), bot.getNick());
        return bot;
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud-serverless.bots.mirai", name = "enabled", havingValue = "true")
    public MiraiMessageSender miraiMessageSender(CloudServerlessProperties properties,
                                                 MessageTextSanitizer sanitizer,
                                                 Bot bot) {
        return new MiraiMessageSender(properties, sanitizer, bot);
    }

    @Bean
    @ConditionalOnProperty(prefix = "cloud-serverless.bots.mirai", name = "enabled", havingValue = "false", matchIfMissing = true)
    public NoopMiraiMessageSender noopMiraiMessageSender() {
        return new NoopMiraiMessageSender();
    }

    private BotConfiguration botConfiguration(CloudServerlessProperties.Mirai mirai) {
        BotConfiguration botConfiguration = new BotConfiguration();
        botConfiguration.noNetworkLog();
        botConfiguration.setCacheDir(new File("./cache"));
        botConfiguration.fileBasedDeviceInfo("./device.json");
        botConfiguration.setProtocol(BotConfiguration.MiraiProtocol.valueOf(mirai.getProtocol()));
        botConfiguration.autoReconnectOnForceOffline();
        botConfiguration.setHeartbeatTimeoutMillis(10 * 1000);
        botConfiguration.setReconnectionRetryTimes(24 * 60 * 60 * 1000);
        return botConfiguration;
    }
}
