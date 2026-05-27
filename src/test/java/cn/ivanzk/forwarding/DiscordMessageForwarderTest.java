package cn.ivanzk.forwarding;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import cn.ivanzk.bot.MessageTextSanitizer;
import cn.ivanzk.bot.kook.KookMessageSender;
import cn.ivanzk.bot.mirai.MiraiMessageSender;
import cn.ivanzk.config.CloudServerlessProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.Nullable;

class DiscordMessageForwarderTest {

    @Test
    void shouldForwardMatchingDiscordMessageToBothTargets() {
        CloudServerlessProperties properties = new CloudServerlessProperties();
        properties.getForwarding().getDiscordToMirai().setEnabled(true);
        properties.getForwarding().getDiscordToMirai().setChannelNames(List.of("更新"));
        properties.getForwarding().getDiscordToKook().setEnabled(true);
        properties.getForwarding().getDiscordToKook().setChannelNames(List.of("更新"));
        CapturingMiraiSender mirai = new CapturingMiraiSender(properties);
        CapturingKookSender kook = new CapturingKookSender(properties);
        DiscordMessageForwarder forwarder = new DiscordMessageForwarder(
                properties,
                singletonProvider(mirai),
                singletonProvider(kook)
        );

        forwarder.forward("游戏更新", "Hello <123>");

        assertThat(mirai.messages).containsExactly("Hello <123>");
        assertThat(kook.messages).containsExactly("游戏更新:Hello <123>");
    }

    @Test
    void shouldSkipWhenChannelDoesNotMatch() {
        CloudServerlessProperties properties = new CloudServerlessProperties();
        properties.getForwarding().getDiscordToMirai().setEnabled(true);
        properties.getForwarding().getDiscordToMirai().setChannelNames(List.of("更新"));
        CapturingMiraiSender mirai = new CapturingMiraiSender(properties);
        DiscordMessageForwarder forwarder = new DiscordMessageForwarder(
                properties,
                singletonProvider(mirai),
                emptyProvider()
        );

        forwarder.forward("聊天", "Hello");

        assertThat(mirai.messages).isEmpty();
    }

    @Test
    void shouldSanitizeMessagesForTargets() {
        MessageTextSanitizer sanitizer = new MessageTextSanitizer();

        assertThat(sanitizer.forMirai("Hello <123>  world")).isEqualTo("Hello world");
        assertThat(sanitizer.forKook("@user: Hello <123>  世界")).isEqualTo("世界");
    }

    private static class CapturingMiraiSender extends MiraiMessageSender {
        private final List<String> messages = new ArrayList<>();

        CapturingMiraiSender(CloudServerlessProperties properties) {
            super(properties, new MessageTextSanitizer(), null);
        }

        @Override
        public void sendBroadcast(String content) {
            messages.add(content);
        }
    }

    private static class CapturingKookSender extends KookMessageSender {
        private final List<String> messages = new ArrayList<>();

        CapturingKookSender(CloudServerlessProperties properties) {
            super(properties, null, new MessageTextSanitizer());
        }

        @Override
        public void sendChannelMessage(String channelName, String content, boolean sanitize) {
            messages.add(channelName + ":" + content);
        }
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
