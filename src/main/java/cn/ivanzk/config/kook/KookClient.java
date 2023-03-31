package cn.ivanzk.config.kook;

import com.github.houbb.opencc4j.util.ZhTwConverterUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.java.comn.assist.DataCache;
import com.java.comn.assist.GenericClazz;
import com.java.comn.assist.MapWrap;
import com.java.comn.util.JacksonUtil;
import com.java.comn.util.SmallTool;
import com.net.comn.http.HttpClientProxy;
import com.net.comn.server.ServerContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
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

    /**
     * 服务器缓存
     */
    public static DataCache<String, List<Guild>> guildCache = new DataCache<String, List<Guild>>(10 * 60 * 1000L) {
        @Override
        public List<Guild> query(String guildId) {
            if (SmallTool.isEmpty(guildId)) {
                return guildList();
            } else {
                return Lists.newArrayList(guildView(guildId));
            }
        }
    };
    /**
     * 频道缓存
     */
    public static DataCache<String, List<Channel>> channelCache = new DataCache<String, List<Channel>>(10 * 60 * 1000L) {
        @Override
        public List<Channel> query(String channelId) {
            if (SmallTool.isEmpty(channelId)) {
                List<Channel> channels = Lists.newArrayList();
                List<Guild> guilds = guildCache.get(null);
                for (Guild guild : guilds) {
                    Channel[] channelArr = guild.getChannels();
                    if (SmallTool.notEmpty(channelArr)) {
                        channels.addAll(Arrays.asList(channelArr));
                    }
                }
                return channels;
            } else {
                return Lists.newArrayList(channelView(channelId));
            }
        }
    };

    public void channelMessage(String channelName, String message) {
        KookProperties kookProperties = ServerContext.getBean(KookProperties.class);
        if (kookProperties == null) {
            return;
        }
        channelCache.get(null).forEach(channel -> {
            if (channel.getName().indexOf(channelName) >= 0 || channel.getName().indexOf(ZhTwConverterUtil.toTraditional(channelName)) >= 0) {
                channelMessage(kookProperties, channel.getId(), formatMessage(message));
            }
        });
    }

    public void channelImg(String channelName, String imgUrl) {
        KookProperties kookProperties = ServerContext.getBean(KookProperties.class);
        if (kookProperties == null) {
            return;
        }
        channelCache.get(null).forEach(channel -> {
            if (channel.getName().indexOf(channelName) >= 0 || channel.getName().indexOf(ZhTwConverterUtil.toTraditional(channelName)) >= 0) {
                channelMessage(kookProperties, channel.getId(), imgUrl);
            }
        });
    }

    public void directMessage(String userId, String message) {
        KookProperties kookProperties = ServerContext.getBean(KookProperties.class);
        if (kookProperties == null) {
            return;
        }
        userId = SmallTool.isEmpty(userId) ? kookProperties.getAdmin() : userId;
        directMessage(kookProperties, userId, formatMessage(message));
    }

    public static void channelMessage(KookProperties kookProperties, String channelId, String content) {
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
        httpClientProxy.clearInvalidConnection();
    }

    public static void directMessage(KookProperties kookProperties, String userId, String content) {
        try {
            String url = kookProperties.getBaseUrl() + kookProperties.getDirectMessage();
            Map<String, Object> header = Maps.newHashMap();
            header.put("Authorization", "Bot " + kookProperties.getToken());
            MapWrap body = new MapWrap();
            body.put("target_id", userId);
            body.put("content", content);
            httpClientProxy.doPostJsonForResult(url, body, header);
        } catch (Exception e) {
            e.printStackTrace();
        }
        httpClientProxy.clearInvalidConnection();
    }

    public static List<Guild> guildList() {
        KookProperties kookProperties = ServerContext.getBean(KookProperties.class);
        if (kookProperties == null) {
            return Lists.newArrayList();
        }
        try {
            Map<String, Object> header = Maps.newHashMap();
            header.put("Authorization", "Bot " + kookProperties.getToken());
            String url = kookProperties.getBaseUrl() + kookProperties.getGuildList();
            String jsonString = httpClientProxy.doGetForResult(url, header);
            MapWrap result = JacksonUtil.fromJson(jsonString, MapWrap.class);
            List<Map<String, Object>> list = result.get("data.items", GenericClazz.getArrayListClazz());
            List<Guild> guilds = list.stream().map(s -> JacksonUtil.fromJson(JacksonUtil.toJson(s), Guild.class)).collect(Collectors.toList());
            for (Guild guild : guilds) {
                Guild guildView = guildView(guild.getId());
                if (guildView != null) {
                    guild.setChannels(guildView.getChannels());
                }
            }
            return guilds;
        } catch (Exception e) {
            e.printStackTrace();
        }
        httpClientProxy.clearInvalidConnection();
        return Lists.newArrayList();
    }

    public static Guild guildView(String guildId) {
        KookProperties kookProperties = ServerContext.getBean(KookProperties.class);
        if (kookProperties == null) {
            return null;
        }
        try {
            Map<String, Object> header = Maps.newHashMap();
            header.put("Authorization", "Bot " + kookProperties.getToken());
            String url = kookProperties.getBaseUrl() + kookProperties.getGuildView() + "?guild_id=" + guildId;
            String jsonString = httpClientProxy.doGetForResult(url, header);
            MapWrap result = JacksonUtil.fromJson(jsonString, MapWrap.class);
            Object guildData = result.get("data");
            Guild guild = JacksonUtil.fromJson(JacksonUtil.toJson(guildData), Guild.class);
            return guild;
        } catch (Exception e) {
            e.printStackTrace();
        }
        httpClientProxy.clearInvalidConnection();
        return null;
    }

    public static List<Channel> channelList(String guildId) {
        KookProperties kookProperties = ServerContext.getBean(KookProperties.class);
        if (kookProperties == null) {
            return Lists.newArrayList();
        }
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
        httpClientProxy.clearInvalidConnection();
        return Lists.newArrayList();
    }

    public static Channel channelView(String channelId) {
        KookProperties kookProperties = ServerContext.getBean(KookProperties.class);
        if (kookProperties == null) {
            return null;
        }
        try {
            String url = kookProperties.getBaseUrl() + kookProperties.getChannelView() + "?target_id=" + channelId;
            Map<String, Object> header = Maps.newHashMap();
            header.put("Authorization", "Bot " + kookProperties.getToken());
            String jsonString = httpClientProxy.doGetForResult(url, header);
            MapWrap result = JacksonUtil.fromJson(jsonString, MapWrap.class);
            Object guildData = result.get("data");
            Channel channel = JacksonUtil.fromJson(JacksonUtil.toJson(guildData), Channel.class);
            return channel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        httpClientProxy.clearInvalidConnection();
        return null;
    }

    private static String formatMessage(String message) {
        return message
                .replaceAll("[A-Za-z_@:]", "")
                .replaceAll("[<]{1}[\\d\\D]+[>]{1}", "")
                .replaceAll("[ ]+", " ");
    }
}
