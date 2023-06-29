package cn.ivanzk.config.mirai;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.auth.BotAuthorization;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

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
    @ConditionalOnBean(MiraiBotProperties.class)
    public BotConfiguration botConfiguration(MiraiBotProperties miraiBotProperties) throws FileNotFoundException {
        String classpath = ResourceUtils.getURL("classpath:").getPath();
        System.out.println(classpath);
        BotConfiguration botConfiguration = new BotConfiguration() {{
            noNetworkLog();
            setCacheDir(new File(classpath + "/cache"));
            fileBasedDeviceInfo(classpath + "/device.json");
            setProtocol(miraiBotProperties.getProtocol());
            autoReconnectOnForceOffline();
        }};
        botConfiguration.setHeartbeatTimeoutMillis(10 * 1000);
        botConfiguration.setReconnectionRetryTimes(24 * 60 * 60 * 1000);
        return botConfiguration;
    }

    @Bean
    @ConditionalOnBean(value = {MiraiBotProperties.class, BotConfiguration.class})
    public Bot bot(MiraiBotProperties miraiBotProperties, BotConfiguration botConfiguration) {
        Bot bot = BotFactory.INSTANCE.newBot(miraiBotProperties.getQq(), BotAuthorization.byQRCode(), botConfiguration);
        bot.login();
        if (bot.isOnline()) {
            bot.getFriend(miraiBotProperties.getAdmin()).sendMessage(String.format("QQ:%s<%s>上线", bot.getNick(), bot.getId()));
        }
        return bot;
    }
}
