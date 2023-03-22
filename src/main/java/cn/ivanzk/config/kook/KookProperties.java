package cn.ivanzk.config.kook;

/**
 * @author zk
 */
public class KookProperties {
    private String token;
    private String admin;
    private String baseUrl;
    private String guildList;
    private String guildView;
    private String channelList;
    private String channelView;
    private String channelMessage;
    private String uploadFile;
    private String directMessage;

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAdmin() {
        return this.admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
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

    public String getGuildView() {
        return this.guildView;
    }

    public void setGuildView(String guildView) {
        this.guildView = guildView;
    }

    public String getChannelList() {
        return this.channelList;
    }

    public void setChannelList(String channelList) {
        this.channelList = channelList;
    }

    public String getChannelView() {
        return this.channelView;
    }

    public void setChannelView(String channelView) {
        this.channelView = channelView;
    }

    public String getChannelMessage() {
        return this.channelMessage;
    }

    public void setChannelMessage(String channelMessage) {
        this.channelMessage = channelMessage;
    }

    public String getUploadFile() {
        return this.uploadFile;
    }

    public void setUploadFile(String uploadFile) {
        this.uploadFile = uploadFile;
    }

    public String getDirectMessage() {
        return this.directMessage;
    }

    public void setDirectMessage(String directMessage) {
        this.directMessage = directMessage;
    }
}
