package cn.ivanzk.config.mirai;

import net.mamoe.mirai.utils.BotConfiguration;

/**
 * @author zk
 */
public class MiraiBotProperties {
    private Long qq;
    private String password;
    private Long admin;
    private Long[] friends;
    private Long[] groups;
    private String deviceInfoPath = "device.json";
    private BotConfiguration.MiraiProtocol protocol = BotConfiguration.MiraiProtocol.ANDROID_WATCH;

    public Long getQq() {
        return this.qq;
    }

    public void setQq(Long qq) {
        this.qq = qq;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getAdmin() {
        return this.admin;
    }

    public void setAdmin(Long admin) {
        this.admin = admin;
    }

    public Long[] getFriends() {
        return this.friends;
    }

    public void setFriends(Long[] friends) {
        this.friends = friends;
    }

    public Long[] getGroups() {
        return this.groups;
    }

    public void setGroups(Long[] groups) {
        this.groups = groups;
    }

    public String getDeviceInfoPath() {
        return this.deviceInfoPath;
    }

    public void setDeviceInfoPath(String deviceInfoPath) {
        this.deviceInfoPath = deviceInfoPath;
    }

    public BotConfiguration.MiraiProtocol getProtocol() {
        return this.protocol;
    }

    public void setProtocol(BotConfiguration.MiraiProtocol protocol) {
        this.protocol = protocol;
    }
}
