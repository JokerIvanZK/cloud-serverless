package cn.ivanzk.server.impl;

import cn.ivanzk.config.discord.DiscordProperties;
import cn.ivanzk.config.kook.KookClient;
import cn.ivanzk.server.BasicService;
import com.google.common.collect.Maps;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Discord消息转发到QQ
 *
 * @author zk
 */
@Component
@ConditionalOnProperty(name = "service.discord-forward-kook", havingValue = "true")
@ConditionalOnBean({GatewayDiscordClient.class, KookClient.class})
public class DiscordForwardKook implements BasicService {
    @Autowired
    private GatewayDiscordClient gatewayDiscordClient;
    @Autowired
    private DiscordProperties discordProperties;
    @Autowired
    private KookClient kookClient;

    private static Map<String, String> channelMap = Maps.newHashMap();
    private static final String forwardChannelNames = "债券";


    @Override
    public void start() {
        try {
            gatewayDiscordClient.on(ReadyEvent.class).subscribe(event -> {
                gatewayDiscordClient.getGuildById(Snowflake.of(discordProperties.getOwnGuildId())).single().block().getChannels().subscribe(channel -> {
                    channelMap.put(channel.getId().asString(), channel.getName());
                });
                System.out.println(channelMap.toString());
            });
            gatewayDiscordClient.on(MessageCreateEvent.class).subscribe(event -> {
                final Message message = event.getMessage();
                final MessageChannel channel = message.getChannel().block();
                String channelId = channel.getId().asString();
                String channelName = channelMap.get(channelId);
                if (forwardChannelNames.contains(channelName)) {
                    kookClient.sendMessage(channelName, message.getContent());
                }
            });
            gatewayDiscordClient.onDisconnect().block();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}