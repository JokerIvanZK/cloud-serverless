package cn.ivanzk.forwarding;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.MessageChannel;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Discord 消息事件监听。
 */
@Component
@ConditionalOnBean(GatewayDiscordClient.class)
public class DiscordMessageListener {

    private static final Logger log = LoggerFactory.getLogger(DiscordMessageListener.class);

    private final GatewayDiscordClient gatewayDiscordClient;
    private final DiscordMessageForwarder forwarder;

    public DiscordMessageListener(GatewayDiscordClient gatewayDiscordClient, DiscordMessageForwarder forwarder) {
        this.gatewayDiscordClient = gatewayDiscordClient;
        this.forwarder = forwarder;
    }

    @PostConstruct
    public void subscribe() {
        gatewayDiscordClient.on(MessageCreateEvent.class)
                .flatMap(this::handle)
                .subscribe(null, error -> log.warn("Discord message listener failed: {}", error.getMessage()));
    }

    private Mono<Void> handle(MessageCreateEvent event) {
        Message message = event.getMessage();
        return message.getChannel()
                .flatMap(channel -> resolveChannelName(channel)
                        .defaultIfEmpty(String.valueOf(channel.getId().asLong())))
                .doOnNext(channelName -> forwarder.forward(channelName, message.getContent()))
                .then();
    }

    private Mono<String> resolveChannelName(MessageChannel channel) {
        Snowflake channelId = channel.getId();
        return gatewayDiscordClient.getGuilds()
                .flatMap(guild -> resolveGuildChannel(guild, channelId))
                .map(GuildChannel::getName)
                .next();
    }

    private Mono<GuildChannel> resolveGuildChannel(Guild guild, Snowflake channelId) {
        return guild.getChannelById(channelId)
                .onErrorResume(error -> Mono.empty());
    }
}
