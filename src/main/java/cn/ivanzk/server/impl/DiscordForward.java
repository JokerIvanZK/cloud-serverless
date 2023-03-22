package cn.ivanzk.server.impl;

import cn.ivanzk.config.kook.KookClient;
import cn.ivanzk.config.mirai.MiraiBot;
import cn.ivanzk.server.BasicService;
import com.java.comn.assist.DataCache;
import com.java.comn.util.DigitStyle;
import com.java.comn.util.SmallTool;
import com.net.comn.server.ServerContext;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.MessageChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Discord消息转发到QQ
 *
 * @author zk
 */
@Component
@ConditionalOnExpression("${service.discord-forward-mirai:true} || ${discord-forward-kook:true}")
@ConditionalOnBean(GatewayDiscordClient.class)
public class DiscordForward implements BasicService {
    @Autowired
    private GatewayDiscordClient gatewayDiscordClient;
    @Autowired(required = false)
    private MiraiBot miraiBot;
    @Autowired(required = false)
    private KookClient kookClient;

    /**
     * dc频道缓存
     */
    public static DataCache<Long, String> channelCache = new DataCache<Long, String>(10 * 60 * 1000L) {
        @Override
        public String query(Long channelId) {
            GatewayDiscordClient gatewayDiscordClient = ServerContext.getBean(GatewayDiscordClient.class);
            List<Guild> guilds = gatewayDiscordClient.getGuilds().collect(Collectors.toList()).single().block();
            for (Guild guild : guilds) {
                GuildChannel channel = guild.getChannelById(Snowflake.of(channelId)).single().block();
                if (channel == null) {
                    continue;
                }
                String channelName = channel.getName();
                if (SmallTool.notEmpty(channelName)) {
                    return channelName;
                }
            }
            return null;
        }
    };
    @Value("${service.mirai-forward-channel-names}")
    private String miraiForwardChannelNames = "";
    @Value("${service.kook-forward-channel-names}")
    private String kookForwardChannelNames = "";

    @Override
    public void start() {
        try {
            gatewayDiscordClient.on(MessageCreateEvent.class).subscribe(event -> {
                final Message message = event.getMessage();
                final MessageChannel channel = message.getChannel().block();
                Long channelId = channel.getId().asLong();
                String channelName = channelCache.get(channelId);
                if (miraiBot != null && miraiForwardChannelNames.contains(channelName)) {
                    miraiBot.sendMessage(TimestampUtil.matcherTimeStamp(message.getContent()));
                }
                if (kookClient != null && kookForwardChannelNames.contains(channelName)) {
                    kookClient.channelMessage(channelName, message.getContent());
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