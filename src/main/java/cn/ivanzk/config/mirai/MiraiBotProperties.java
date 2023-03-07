package cn.ivanzk.config.mirai;

/**
 * @author zk
 */
public class MiraiBotProperties {
    private Long qq;
    private String password;
    private String deviceInfoPath;
    private Long admin;
    private Long[] friends;
    private Long[] groups;

    public Long getQq() {
        return qq;
    }

    public void setQq(Long qq) {
        this.qq = qq;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceInfoPath() {
        return this.deviceInfoPath;
    }

    public void setDeviceInfoPath(String deviceInfoPath) {
        this.deviceInfoPath = deviceInfoPath;
    }

    public Long getAdmin() {
        return admin;
    }

    public void setAdmin(Long admin) {
        this.admin = admin;
    }

    public Long[] getFriends() {
        return friends;
    }

    public void setFriends(Long[] friends) {
        this.friends = friends;
    }

    public Long[] getGroups() {
        return groups;
    }

    public void setGroups(Long[] groups) {
        this.groups = groups;
    }
}
