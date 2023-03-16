package cn.ivanzk.config.kook;

/**
 * @author zk
 */
public class KookProperties {
    private String token;
    private String baseUrl;
    private String guildList;
    private String channelList;
    private String channelMessage;

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getGuildList() {
        return this.guildList;
    }

    public void setGuildList(String guildList) {
        this.guildList = guildList;
    }

    public String getChannelList() {
        return this.channelList;
    }

    public void setChannelList(String channelList) {
        this.channelList = channelList;
    }

    public String getChannelMessage() {
        return this.channelMessage;
    }

    public void setChannelMessage(String channelMessage) {
        this.channelMessage = channelMessage;
    }
}
