package cn.ivanzk.config.discord;

/**
 * @author zk
 */
public class DiscordForwardProperties {
    public boolean mirai = false;
    public boolean kook = false;

    public boolean getMirai() {
        return this.mirai;
    }

    public void setMirai(boolean mirai) {
        this.mirai = mirai;
    }

    public boolean getKook() {
        return this.kook;
    }

    public void setKook(boolean kook) {
        this.kook = kook;
    }
}
