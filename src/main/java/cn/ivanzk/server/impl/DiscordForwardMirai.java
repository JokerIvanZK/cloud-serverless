package cn.ivanzk.server.impl;

import cn.ivanzk.config.mirai.MiraiBot;
import cn.ivanzk.server.BasicService;
import com.java.comn.util.DigitStyle;
import com.java.comn.util.SmallTool;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
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
@ConditionalOnProperty(name = "service.discord-forward-mirai", havingValue = "true")
@ConditionalOnBean({GatewayDiscordClient.class, MiraiBot.class})
public class DiscordForwardMirai implements BasicService {
    @Autowired
    private GatewayDiscordClient gatewayDiscordClient;
    @Autowired
    private MiraiBot miraiBot;

    @Override
    public void start() {
        try {
            gatewayDiscordClient.on(ReadyEvent.class).subscribe(event -> miraiBot.noticeAdmin(String.format("discord:<%s>上线", event.getSelf().getUsername())));
            gatewayDiscordClient.on(MessageCreateEvent.class).subscribe(event -> {
                final Message message = event.getMessage();
                miraiBot.sendMessage(TimestampUtil.matcherTimeStamp(message.getContent()));
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