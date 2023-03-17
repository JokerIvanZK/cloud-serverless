package cn.ivanzk.config.kook;

import com.google.common.collect.Maps;
import com.java.comn.assist.GenericClazz;
import com.java.comn.assist.MapWrap;
import com.java.comn.util.JacksonUtil;
import com.net.comn.http.HttpClientProxy;
import com.net.comn.server.ServerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zk
 */
@Configuration
@ConditionalOnProperty(name = "kook.enable", havingValue = "true")
public class KookClient {
    private static Map<String, String> guildMap = Maps.newHashMap();
    private static Map<String, String> channelMap = Maps.newHashMap();
    private static HttpClientProxy httpClientProxy = new HttpClientProxy();

    @Bean
    @ConfigurationProperties(prefix = "kook")
    public KookProperties kookProperties() {
        return new KookProperties();
    }

    @Bean
    @Autowired
    @DependsOn("kookProperties")
    public Object kookClientInit(KookProperties kookProperties) {
        List<Guild> guilds = guildList(kookProperties);
        guilds.forEach(guild -> {
            String guildId = guild.getId();
            String guildName = guild.getName();
            guildMap.put(guildId, guildName);
            List<Channel> channels = channelList(kookProperties, guildId);
            channels.forEach(channel -> {
                String channelId = channel.getId();
                String channelName = channel.getName();
                channelMap.put(channelId, channelName);
            });
        });
        return null;
    }

    public void sendMessage(String channelName, String message) {
        KookProperties kookProperties = ServerContext.getBean(KookProperties.class);
        if (kookProperties == null) {
            return;
        }
        channelMap.forEach((id, name) -> {
            if (name.indexOf(channelName) >= 0) {
                channelMessage(kookProperties, id, formatMessage(message));
            }
//            if (name.indexOf(channelName) >= 0 || name.indexOf(ZhTwConverterUtil.toTraditional(channelName)) >= 0) {
//                channelMessage(kookProperties, id, formatMessage(message));
//            }
        });
    }

    public List<Guild> guildList(KookProperties kookProperties) {
        try {
            String url = kookProperties.getBaseUrl() + kookProperties.getGuildList();
            Map<String, Object> header = Maps.newHashMap();
            header.put("Authorization", "Bot " + kookProperties.getToken());
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

    public List<Channel> channelList(KookProperties kookProperties, String guildId) {
        try {
            String url = kookProperties.getBaseUrl() + kookProperties.getChannelList() + "?guild_id=" + guildId;
            Map<String, Object> header = Maps.newHashMap();
            header.put("Authorization", "Bot " + kookProperties.getToken());
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

    public void channelMessage(KookProperties kookProperties, String channelId, String content) {
        try {
            String url = kookProperties.getBaseUrl() + kookProperties.getChannelMessage();
            Map<String, Object> header = Maps.newHashMap();
            header.put("Authorization", "Bot " + kookProperties.getToken());
            MapWrap body = new MapWrap();
            body.put("target_id", channelId);
            body.put("content", content);
            httpClientProxy.doPostJsonForResult(url, body, header);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String formatMessage(String message) {
        return message
                .replaceAll(":hl:", "")
                .replaceAll(":sfbtm:", "")
                .replaceAll(":seboth:", "")
                .replaceAll(":setop:", "")
                .replaceAll(":bsb_fabric:", "")
                .replaceAll(":bsb_lumber:", "")
                .replaceAll(":bsb_leather:", "")
                .replaceAll(":bsb_iron:", "")
                .replaceAll(":bsb:", "")
                .replaceAll("[(A-Za-z)]", "")
                .replaceAll("@", "")
                .replaceAll("[<]+[\\d]+[>]+", "")
                .replaceAll("[ ]+", " ");
    }

}
