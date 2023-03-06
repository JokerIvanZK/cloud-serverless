package com.example.demo.config.mirai;

import org.springframework.util.ResourceUtils;

import java.io.File;

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
        try {
            String classpath = ResourceUtils.getURL("classpath:").getPath();
            this.deviceInfoPath = classpath + this.deviceInfoPath;
            File file = new File(this.deviceInfoPath);
            if (!file.exists()) {
                file.mkdir();
            }
            return this.deviceInfoPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceInfoPath;
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
