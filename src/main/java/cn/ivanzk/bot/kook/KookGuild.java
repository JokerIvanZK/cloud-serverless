package cn.ivanzk.bot.kook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Kook 服务器摘要。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class KookGuild {

    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
