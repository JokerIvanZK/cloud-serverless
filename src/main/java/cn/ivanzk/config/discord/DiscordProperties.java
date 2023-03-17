package cn.ivanzk.config.discord;

/**
 * @author zk
 */
public class DiscordProperties {
    private String token;
    private String ownGuildId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOwnGuildId() {
        return this.ownGuildId;
    }

    public void setOwnGuildId(String ownGuildId) {
        this.ownGuildId = ownGuildId;
    }
}
