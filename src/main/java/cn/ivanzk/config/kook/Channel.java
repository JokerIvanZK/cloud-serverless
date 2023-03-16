package cn.ivanzk.config.kook;

/**
 * @author zk
 */
public class Channel {
    private String id;
    private String user_id;
    private String parent_id;
    private String name;
    private int type;
    private int level;
    private int limit_amount;
    private boolean is_category;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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
}
