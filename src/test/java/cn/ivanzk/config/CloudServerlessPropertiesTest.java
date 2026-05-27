package cn.ivanzk.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;

class CloudServerlessPropertiesTest {

    @Test
    void shouldBindDevConfiguration() throws Exception {
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        StandardEnvironment environment = new StandardEnvironment();
        MutablePropertySources sources = environment.getPropertySources();
        sources.addLast(loader.load("dev", new ClassPathResource("application-dev.yml")).get(0));

        CloudServerlessProperties properties = Binder.get(environment)
                .bind("cloud-serverless", Bindable.of(CloudServerlessProperties.class))
                .orElseThrow(() -> new IllegalStateException("cloud-serverless properties were not bound"));

        assertThat(properties.getBots().getDiscord().isEnabled()).isFalse();
        assertThat(properties.getBots().getDiscord().getToken()).isEmpty();
        assertThat(properties.getForwarding().getDiscordToKook().getChannelNames())
                .containsExactly("债券", "admin测试");
        assertThat(properties.getSchedule().getNewsCron()).isEqualTo("0 0/5 * * * ?");
    }
}
