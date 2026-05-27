package cn.ivanzk.bot;

/**
 * 机器人运行状态。
 */
public record BotStatus(boolean enabled, boolean available, boolean online, String message) {

    public static BotStatus disabled() {
        return new BotStatus(false, false, false, "disabled");
    }

    public static BotStatus unavailable(String message) {
        return new BotStatus(true, false, false, message);
    }

    public static BotStatus onlineStatus() {
        return new BotStatus(true, true, true, "online");
    }

    public static BotStatus offline(String message) {
        return new BotStatus(true, true, false, message);
    }
}
