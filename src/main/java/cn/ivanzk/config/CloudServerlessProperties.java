package cn.ivanzk.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * cloud-serverless 统一配置。
 */
@ConfigurationProperties(prefix = "cloud-serverless")
public class CloudServerlessProperties {

    private final Bots bots = new Bots();
    private final Forwarding forwarding = new Forwarding();
    private final News news = new News();
    private final Schedule schedule = new Schedule();

    public Bots getBots() {
        return bots;
    }

    public Forwarding getForwarding() {
        return forwarding;
    }

    public News getNews() {
        return news;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public static class Bots {
        private final Discord discord = new Discord();
        private final Kook kook = new Kook();
        private final Mirai mirai = new Mirai();

        public Discord getDiscord() {
            return discord;
        }

        public Kook getKook() {
            return kook;
        }

        public Mirai getMirai() {
            return mirai;
        }
    }

    public static class Discord {
        private boolean enabled;
        private String token;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public static class Kook {
        private boolean enabled;
        private String token;
        private String admin;
        private String baseUrl = "https://www.kookapp.cn/";
        private final KookEndpoints endpoints = new KookEndpoints();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getAdmin() {
            return admin;
        }

        public void setAdmin(String admin) {
            this.admin = admin;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public KookEndpoints getEndpoints() {
            return endpoints;
        }
    }

    public static class KookEndpoints {
        private String guildList = "/api/v3/guild/list";
        private String channelList = "/api/v3/channel/list";
        private String channelMessage = "/api/v3/channel/message";
        private String directMessage = "/api/v3/direct-message/create";

        public String getGuildList() {
            return guildList;
        }

        public void setGuildList(String guildList) {
            this.guildList = guildList;
        }

        public String getChannelList() {
            return channelList;
        }

        public void setChannelList(String channelList) {
            this.channelList = channelList;
        }

        public String getChannelMessage() {
            return channelMessage;
        }

        public void setChannelMessage(String channelMessage) {
            this.channelMessage = channelMessage;
        }

        public String getDirectMessage() {
            return directMessage;
        }

        public void setDirectMessage(String directMessage) {
            this.directMessage = directMessage;
        }
    }

    public static class Mirai {
        private boolean enabled;
        private Long qq;
        private Long admin;
        private List<Long> friends = new ArrayList<>();
        private List<Long> groups = new ArrayList<>();
        private String protocol = "MACOS";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Long getQq() {
            return qq;
        }

        public void setQq(Long qq) {
            this.qq = qq;
        }

        public Long getAdmin() {
            return admin;
        }

        public void setAdmin(Long admin) {
            this.admin = admin;
        }

        public List<Long> getFriends() {
            return friends;
        }

        public void setFriends(List<Long> friends) {
            this.friends = defaultList(friends);
        }

        public List<Long> getGroups() {
            return groups;
        }

        public void setGroups(List<Long> groups) {
            this.groups = defaultList(groups);
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }
    }

    public static class Forwarding {
        private final ForwardRule discordToMirai = new ForwardRule();
        private final ForwardRule discordToKook = new ForwardRule();

        public ForwardRule getDiscordToMirai() {
            return discordToMirai;
        }

        public ForwardRule getDiscordToKook() {
            return discordToKook;
        }
    }

    public static class ForwardRule {
        private boolean enabled;
        private List<String> channelNames = new ArrayList<>();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<String> getChannelNames() {
            return channelNames;
        }

        public void setChannelNames(List<String> channelNames) {
            this.channelNames = defaultList(channelNames);
        }
    }

    public static class News {
        private final ArcheAge archeage = new ArcheAge();

        public ArcheAge getArcheage() {
            return archeage;
        }
    }

    public static class ArcheAge {
        private boolean enabled;
        private String baseUrl = "https://asia.archeage.com";
        private String listPath = "/news?lang=zh_TW";
        private String lastPushUrl = "/news/4740?page=1";
        private String targetChannel = "更新";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getListPath() {
            return listPath;
        }

        public void setListPath(String listPath) {
            this.listPath = listPath;
        }

        public String getLastPushUrl() {
            return lastPushUrl;
        }

        public void setLastPushUrl(String lastPushUrl) {
            this.lastPushUrl = lastPushUrl;
        }

        public String getTargetChannel() {
            return targetChannel;
        }

        public void setTargetChannel(String targetChannel) {
            this.targetChannel = targetChannel;
        }
    }

    public static class Schedule {
        private String heartbeatCron = "0 0 17 * * ?";
        private String newsCron = "0 0/5 * * * ?";

        public String getHeartbeatCron() {
            return heartbeatCron;
        }

        public void setHeartbeatCron(String heartbeatCron) {
            this.heartbeatCron = heartbeatCron;
        }

        public String getNewsCron() {
            return newsCron;
        }

        public void setNewsCron(String newsCron) {
            this.newsCron = newsCron;
        }
    }

    private static <T> List<T> defaultList(List<T> value) {
        return value == null ? new ArrayList<>() : value;
    }
}
