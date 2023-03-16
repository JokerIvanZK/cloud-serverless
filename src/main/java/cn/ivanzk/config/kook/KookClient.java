package cn.ivanzk.config.kook;

import com.google.common.collect.Maps;
import com.java.comn.assist.GenericClazz;
import com.java.comn.assist.MapWrap;
import com.java.comn.util.JacksonUtil;
import com.net.comn.http.HttpClientProxy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zk
 */
@Configuration
@ConditionalOnProperty(name = "kook.enable", havingValue = "true")
public class KookClient {
    private static HttpClientProxy httpClientProxy = new HttpClientProxy();

    @Bean
    @ConfigurationProperties(prefix = "kook")
    public KookProperties kookProperties() {
        return new KookProperties();
    }

    public void sendMessage(String message) {
        List<Guild> guilds = guildList();
        for (Guild guild : guilds) {
            String guildId = guild.getId();
            List<Channel> channels = channelList(guildId);
            for (Channel channel : channels) {
                if (channel.getName().indexOf("债券") >= 0) {
                    channelMessage(channel.getId(), message);
                }
            }
        }
    }

    public List<Guild> guildList() {
        try {
            String url = kookProperties().getBaseUrl() + kookProperties().getGuildList();
            Map<String, Object> header = Maps.newHashMap();
            header.put("Authorization", "Bot " + kookProperties().getToken());
            String jsonString = httpClientProxy.doGetForResult(url, header);
            MapWrap result = JacksonUtil.fromJson(jsonString, MapWrap.class);
            List<Map<String, Object>> list = result.get("data.items", GenericClazz.getArrayListClazz());
            List<Guild> guilds = list.stream().map(s -> JacksonUtil.fromJson(JacksonUtil.toJson(s), Guild.class)).collect(Collectors.toList());
            return guilds;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Channel> channelList(String guildId) {
        try {
            String url = kookProperties().getBaseUrl() + kookProperties().getChannelList() + "?guild_id=" + guildId;
            Map<String, Object> header = Maps.newHashMap();
            header.put("Authorization", "Bot " + kookProperties().getToken());
            String jsonString = httpClientProxy.doGetForResult(url, header);
            MapWrap result = JacksonUtil.fromJson(jsonString, MapWrap.class);
            List<Map<String, Object>> list = result.get("data.items", GenericClazz.getArrayListClazz());
            List<Channel> channels = list.stream().map(s -> JacksonUtil.fromJson(JacksonUtil.toJson(s), Channel.class))
                    .filter(s -> s != null)
                    .filter(s -> s.getIs_category() == false)
                    .collect(Collectors.toList());
            return channels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void channelMessage(String channelId, String content) {
        try {
            String url = kookProperties().getBaseUrl() + kookProperties().getChannelMessage();
            Map<String, Object> header = Maps.newHashMap();
            header.put("Authorization", "Bot " + kookProperties().getToken());
            MapWrap body = new MapWrap();
            body.put("target_id", channelId);
            body.put("content", content);
            httpClientProxy.doPostJsonForResult(url, body, header);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
