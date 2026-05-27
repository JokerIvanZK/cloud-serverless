package cn.ivanzk.bot.kook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Kook 频道摘要。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class KookChannel {

    private String id;
    private String name;
    @JsonProperty("is_category")
    private boolean category;

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

    public boolean isCategory() {
        return category;
    }

    public void setCategory(boolean category) {
        this.category = category;
    }
}
