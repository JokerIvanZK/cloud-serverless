package cn.ivanzk.web;

import java.util.Map;

import cn.ivanzk.bot.BotStatus;
import cn.ivanzk.bot.discord.DiscordBotStatusProvider;
import cn.ivanzk.bot.kook.KookMessageSender;
import cn.ivanzk.bot.kook.NoopKookMessageSender;
import cn.ivanzk.bot.mirai.MiraiMessageSender;
import cn.ivanzk.bot.mirai.NoopMiraiMessageSender;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查接口。
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final DiscordBotStatusProvider discordStatusProvider;
    private final Object miraiSender;
    private final Object kookSender;

    public HealthController(DiscordBotStatusProvider discordStatusProvider,
                            ObjectProvider<MiraiMessageSender> miraiSender,
                            ObjectProvider<NoopMiraiMessageSender> noopMiraiSender,
                            ObjectProvider<KookMessageSender> kookSender,
                            ObjectProvider<NoopKookMessageSender> noopKookSender) {
        this.discordStatusProvider = discordStatusProvider;
        MiraiMessageSender activeMiraiSender = miraiSender.getIfAvailable();
        this.miraiSender = activeMiraiSender != null ? activeMiraiSender : noopMiraiSender.getIfAvailable(NoopMiraiMessageSender::new);
        KookMessageSender activeKookSender = kookSender.getIfAvailable();
        this.kookSender = activeKookSender != null ? activeKookSender : noopKookSender.getIfAvailable(NoopKookMessageSender::new);
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/state")
    public Map<String, BotStatus> state() {
        return Map.of(
                "discord", discordStatusProvider.status(),
                "mirai", miraiStatus(),
                "kook", kookStatus()
        );
    }

    private BotStatus miraiStatus() {
        if (miraiSender instanceof MiraiMessageSender sender) {
            return sender.status();
        }
        if (miraiSender instanceof NoopMiraiMessageSender sender) {
            return sender.status();
        }
        return BotStatus.disabled();
    }

    private BotStatus kookStatus() {
        if (kookSender instanceof KookMessageSender sender) {
            return sender.status();
        }
        if (kookSender instanceof NoopKookMessageSender sender) {
            return sender.status();
        }
        return BotStatus.disabled();
    }
}
