package cn.ivanzk.web;

import static org.assertj.core.api.Assertions.assertThat;

import cn.ivanzk.bot.BotStatus;
import cn.ivanzk.bot.discord.DiscordBotStatusProvider;
import cn.ivanzk.bot.kook.NoopKookMessageSender;
import cn.ivanzk.bot.mirai.NoopMiraiMessageSender;
import cn.ivanzk.config.CloudServerlessProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.Nullable;

class HealthControllerTest {

    @Test
    void shouldReturnDisabledBotStatesWhenBotsAreNotConfigured() {
        CloudServerlessProperties properties = new CloudServerlessProperties();
        DiscordBotStatusProvider discord = new DiscordBotStatusProvider(properties, emptyProvider());
        HealthController controller = new HealthController(
                discord,
                emptyProvider(),
                singletonProvider(new NoopMiraiMessageSender()),
                emptyProvider(),
                singletonProvider(new NoopKookMessageSender())
        );

        assertThat(controller.ping()).isEqualTo("pong");
        assertThat(controller.state())
                .containsEntry("discord", BotStatus.disabled())
                .containsEntry("mirai", BotStatus.disabled())
                .containsEntry("kook", BotStatus.disabled());
    }

    private static <T> ObjectProvider<T> emptyProvider() {
        return singletonProvider(null);
    }

    private static <T> ObjectProvider<T> singletonProvider(@Nullable T value) {
        return new ObjectProvider<>() {
            @Override
            public T getObject(Object... args) {
                return getObject();
            }

            @Override
            public T getIfAvailable() {
                return value;
            }

            @Override
            public T getIfUnique() {
                return value;
            }

            @Override
            public T getObject() {
                if (value == null) {
                    throw new IllegalStateException("No bean available");
                }
                return value;
            }
        };
    }
}
