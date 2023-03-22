package cn.ivanzk.config.kook;

/**
 * @author zk
 */
public class Channel {
    private String id;
    private String guild_id;
    private String user_id;
    private String parent_id;
    private String name;
    private String topic;
    private int type;
    private int level;
    private int slow_mode;
    private int limit_amount;
    private boolean is_category;
    private String server_url;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGuild_id() {
        return this.guild_id;
    }

    public void setGuild_id(String guild_id) {
        this.guild_id = guild_id;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getParent_id() {
        return this.parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSlow_mode() {
        return this.slow_mode;
    }

    public void setSlow_mode(int slow_mode) {
        this.slow_mode = slow_mode;
    }

    public int getLimit_amount() {
        return this.limit_amount;
    }

    public void setLimit_amount(int limit_amount) {
        this.limit_amount = limit_amount;
    }

    public boolean getIs_category() {
        return this.is_category;
    }

    public void setIs_category(boolean is_category) {
        this.is_category = is_category;
    }

    public String getServer_url() {
        return this.server_url;
    }

    public void setServer_url(String server_url) {
        this.server_url = server_url;
    }
}
