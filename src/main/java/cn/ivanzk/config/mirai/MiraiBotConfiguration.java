package cn.ivanzk.config.mirai;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.cssxsh.mirai.tool.FixProtocolVersion;

import java.util.Map;

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
            autoReconnectOnForceOffline();
            noNetworkLog();
            fileBasedDeviceInfo(miraiBotProperties.getDeviceInfoPath());
            setProtocol(miraiBotProperties.getProtocol());
        }});
        bot.login();
        if (bot.isOnline()) {
            bot.getFriend(miraiBotProperties.getAdmin()).sendMessage(String.format("QQ:%s<%s>上线", bot.getNick(), bot.getId()));
        }
        return bot;
    }

    // 升级协议版本
    public static void update() {
        FixProtocolVersion.update();
    }

    // 获取协议版本信息 你可以用这个来检查update是否正常工作
    public static Map<BotConfiguration.MiraiProtocol, String> info() {
        return FixProtocolVersion.info();
    }
}
