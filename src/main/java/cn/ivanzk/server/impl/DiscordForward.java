package cn.ivanzk.server.impl;

import cn.ivanzk.config.discord.DiscordForwardProperties;
import cn.ivanzk.config.kook.KookClient;
import cn.ivanzk.config.mirai.MiraiBot;
import cn.ivanzk.server.BasicService;
import com.java.comn.util.DigitStyle;
import com.java.comn.util.SmallTool;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Discord消息转发到QQ
 *
 * @author zk
 */
@Component
@ConditionalOnProperty(name = "service.discord-forward.enable", havingValue = "true")
@ConditionalOnBean(GatewayDiscordClient.class)
public class DiscordForward implements BasicService {
    @Autowired
    private DiscordForwardProperties discordForwardProperties;
    @Autowired
    private GatewayDiscordClient gatewayDiscordClient;
    @Autowired(required = false)
    private MiraiBot miraiBot;
    @Autowired(required = false)
    private KookClient kookClient;

    private static final String channel_gx = "700234578195120224";
    private static final String channel_zq = "1085791194367410266";

    @Override
    public void start() {
        try {
            gatewayDiscordClient.on(ReadyEvent.class).subscribe(event -> {
                if (discordForwardProperties.mirai) {
                    miraiBot.noticeAdmin(String.format("discord:<%s>上线", event.getSelf().getUsername()));
                }
            });
            gatewayDiscordClient.on(MessageCreateEvent.class).subscribe(event -> {
                final Message message = event.getMessage();
                final MessageChannel channel = message.getChannel().block();
                String channelId = channel.getId().asString();

                if (discordForwardProperties.mirai) {
                    miraiBot.sendMessage(TimestampUtil.matcherTimeStamp(message.getContent()));
                } else if (discordForwardProperties.kook) {
                    if (SmallTool.isEqual(channel_gx, channelId)) {
                        kookClient.sendMessage(TimestampUtil.matcherTimeStamp(message.getContent()), "更新");
                    }else if(SmallTool.isEqual(channel_zq, channelId)){
                        kookClient.sendMessage(TimestampUtil.matcherTimeStamp(message.getContent()), "债券");
                    }
                }
            });
            gatewayDiscordClient.onDisconnect().block();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    /**
     * 时间处理类
     */
    private static class TimestampUtil {
        private static final String PATTERN = "<t:[0-9]+:f>|<T:[0-9]+:F>|<t:[0-9]+:F>|<T:[0-9]+:f>";

        private static final int TIME_STAMP_LENGTH = 13;

        public static String matcherTimeStamp(String timeStampStr) {
            if (SmallTool.isEmpty(timeStampStr)) {
                return timeStampStr;
            }
            Matcher matcher = Pattern.compile(PATTERN).matcher(timeStampStr);
            while (matcher.find()) {
                StringBuilder buffer = new StringBuilder(timeStampStr);
                int start = matcher.start();
                int end = matcher.end();
                String timeStamp = buffer.substring(start + 3, end - 3);
                timeStampStr = buffer.replace(start, end, timeStampToString(timeStamp)).toString();
                matcher = Pattern.compile(PATTERN).matcher(timeStampStr);
            }
            return timeStampStr;
        }

        public static String timeStampToString(String timeStampStr) {
            if (SmallTool.isEmpty(timeStampStr)) {
                return timeStampStr;
            }
            StringBuilder buffer = new StringBuilder(timeStampStr);
            while (buffer.length() < TIME_STAMP_LENGTH) {
                buffer.append("0");
            }
            Long timeStamp = SmallTool.toLong(buffer.toString(), null);
            if (SmallTool.isEmpty(timeStamp)) {
                return timeStampStr;
            }
            Calendar cal = SmallTool.toCal(timeStamp);
            return DigitStyle.formatDate("yyyy年MM月dd日 HH点mm分", cal.getTime());
        }
    }
}