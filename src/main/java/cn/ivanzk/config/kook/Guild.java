package cn.ivanzk.config.kook;

/**
 * @author zk
 */
public class Guild {

    private String id;

    private String name;

    private String topic;

    private String user_id;

    private String icon;

    private int notify_type;

    private String region;

    private boolean enable_open;

    private String open_id;

    private String default_channel_id;

    private String welcome_channel_id;

    private Integer boost_num;

    private Integer level;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getNotify_type() {
        return this.notify_type;
    }

    public void setNotify_type(int notify_type) {
        this.notify_type = notify_type;
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean getEnable_open() {
        return this.enable_open;
    }

    public void setEnable_open(boolean enable_open) {
        this.enable_open = enable_open;
    }

    public String getOpen_id() {
        return this.open_id;
    }

    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }

    public String getDefault_channel_id() {
        return this.default_channel_id;
    }

    public void setDefault_channel_id(String default_channel_id) {
        this.default_channel_id = default_channel_id;
    }

    public String getWelcome_channel_id() {
        return this.welcome_channel_id;
    }

    public void setWelcome_channel_id(String welcome_channel_id) {
        this.welcome_channel_id = welcome_channel_id;
    }

    public Integer getBoost_num() {
        return this.boost_num;
    }

    public void setBoost_num(Integer boost_num) {
        this.boost_num = boost_num;
    }

    public Integer getLevel() {
        return this.level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
