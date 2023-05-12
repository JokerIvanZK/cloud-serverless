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
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zk
 */
@Configuration
@ConditionalOnProperty(name = "kook.enable", havingValue = "true")
public class KookClient {
    private static final HttpClientProxy httpClientProxy = new HttpClientProxy();

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
            if (channel.getName().contains(channelName) || channel.getName().contains(ZhTwConverterUtil.toTraditional(channelName))) {
                channelMessage(kookProperties, channel.getId(), formatMessage(message));
            }
        });
    }

    public void channelImg(String channelName, String... imgUrl) {
        KookProperties kookProperties = ServerContext.getBean(KookProperties.class);
        if (kookProperties == null) {
            return;
        }
        channelCache.get(null).forEach(channel -> {
            if (channel.getName().contains(channelName) || channel.getName().contains(ZhTwConverterUtil.toTraditional(channelName))) {
                channelCardImage(kookProperties, channel.getId(), channel.getName(), imgUrl);
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
        String url = kookProperties.getBaseUrl() + kookProperties.getChannelMessage();
        Map<String, Object> header = Maps.newHashMap();
        header.put("Authorization", "Bot " + kookProperties.getToken());
        MapWrap body = new MapWrap();
        body.put("target_id", channelId);
        body.put("content", content);
        httpClientProxy.doPostJsonTryForResult(url, body, header);
        httpClientProxy.clearInvalidConnection();
    }

    public static void directMessage(KookProperties kookProperties, String userId, String content) {
        String url = kookProperties.getBaseUrl() + kookProperties.getDirectMessage();
        Map<String, Object> header = Maps.newHashMap();
        header.put("Authorization", "Bot " + kookProperties.getToken());
        MapWrap body = new MapWrap();
        body.put("target_id", userId);
        body.put("content", content);
        httpClientProxy.doPostJsonTryForResult(url, body, header);
        httpClientProxy.clearInvalidConnection();
    }

    public static void channelCardImage(KookProperties kookProperties, String channelId, String title, String... imgUrl) {
        String url = kookProperties.getBaseUrl() + kookProperties.getChannelMessage();
        Map<String, Object> header = Maps.newHashMap();
        header.put("Authorization", "Bot " + kookProperties.getToken());
        MapWrap body = new MapWrap();
        body.put("type", 10);
        body.put("target_id", channelId);
        body.put("content", buildCardMessage(title, imgUrl));
        httpClientProxy.doPostJsonTryForResult(url, body, header);
        httpClientProxy.clearInvalidConnection();
    }

    public static String uploadFile(KookProperties kookProperties, String filePath) {
        String url = kookProperties.getBaseUrl() + kookProperties.getUploadFile();
        String jsonString = null;
        try {
            jsonString = httpClientProxy.doHttpPost(url, (httpClient, httpPost) -> {
                httpPost.addHeader("Authorization", "Bot " + kookProperties.getToken());
                httpPost.setEntity(MultipartEntityBuilder.create().addBinaryBody("file", new File(filePath)).build());
                CloseableHttpResponse response = httpClient.execute(httpPost);
                HttpEntity responseEntity = response.getEntity();
                return EntityUtils.toString(responseEntity, "UTF-8");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        httpClientProxy.clearInvalidConnection();
        MapWrap result = JacksonUtil.fromJson(jsonString, MapWrap.class);
        return result != null ? result.get("data.url", String.class) : null;
    }

    public static List<Guild> guildList() {
        KookProperties kookProperties = ServerContext.getBean(KookProperties.class);
        if (kookProperties == null) {
            return Lists.newArrayList();
        }
        Map<String, Object> header = Maps.newHashMap();
        header.put("Authorization", "Bot " + kookProperties.getToken());
        String url = kookProperties.getBaseUrl() + kookProperties.getGuildList();
        String jsonString = httpClientProxy.doGetTryForResult(url, header);
        MapWrap result = JacksonUtil.fromJson(jsonString, MapWrap.class);
        List<Map<String, Object>> list = result.get("data.items", GenericClazz.getArrayListClazz());
        List<Guild> guilds = list.stream().map(s -> JacksonUtil.fromJson(JacksonUtil.toJson(s), Guild.class)).collect(Collectors.toList());
        for (Guild guild : guilds) {
            Guild guildView = guildView(guild.getId());
            if (guildView != null) {
                guild.setChannels(guildView.getChannels());
            }
        }
        httpClientProxy.clearInvalidConnection();
        return guilds;
    }

    public static Guild guildView(String guildId) {
        KookProperties kookProperties = ServerContext.getBean(KookProperties.class);
        if (kookProperties == null) {
            return null;
        }
        Map<String, Object> header = Maps.newHashMap();
        header.put("Authorization", "Bot " + kookProperties.getToken());
        String url = kookProperties.getBaseUrl() + kookProperties.getGuildView() + "?guild_id=" + guildId;
        String jsonString = httpClientProxy.doGetTryForResult(url, header);
        MapWrap result = JacksonUtil.fromJson(jsonString, MapWrap.class);
        Object guildData = result.get("data");
        Guild guild = JacksonUtil.fromJson(JacksonUtil.toJson(guildData), Guild.class);
        httpClientProxy.clearInvalidConnection();
        return guild;
    }

    public static List<Channel> channelList(String guildId) {
        KookProperties kookProperties = ServerContext.getBean(KookProperties.class);
        if (kookProperties == null) {
            return Lists.newArrayList();
        }
        String url = kookProperties.getBaseUrl() + kookProperties.getChannelList() + "?guild_id=" + guildId;
        Map<String, Object> header = Maps.newHashMap();
        header.put("Authorization", "Bot " + kookProperties.getToken());
        String jsonString = httpClientProxy.doGetTryForResult(url, header);
        MapWrap result = JacksonUtil.fromJson(jsonString, MapWrap.class);
        List<Map<String, Object>> list = result.get("data.items", GenericClazz.getArrayListClazz());
        List<Channel> channels = list.stream().map(s -> JacksonUtil.fromJson(JacksonUtil.toJson(s), Channel.class))
                .filter(Objects::nonNull)
                .filter(s -> !s.getIs_category())
                .collect(Collectors.toList());
        httpClientProxy.clearInvalidConnection();
        return channels;
    }

    public static Channel channelView(String channelId) {
        KookProperties kookProperties = ServerContext.getBean(KookProperties.class);
        if (kookProperties == null) {
            return null;
        }
        String url = kookProperties.getBaseUrl() + kookProperties.getChannelView() + "?target_id=" + channelId;
        Map<String, Object> header = Maps.newHashMap();
        header.put("Authorization", "Bot " + kookProperties.getToken());
        String jsonString = httpClientProxy.doGetTryForResult(url, header);
        MapWrap result = JacksonUtil.fromJson(jsonString, MapWrap.class);
        Object guildData = result.get("data");
        Channel channel = JacksonUtil.fromJson(JacksonUtil.toJson(guildData), Channel.class);
        httpClientProxy.clearInvalidConnection();
        return channel;
    }

    private static String buildCardMessage(String title, String... src) {
        MapWrap cardImage = new MapWrap();
        cardImage.put("type", "card");
        cardImage.put("theme", "secondary");
        cardImage.put("size", "lg");
        List<MapWrap> modules = Lists.newArrayList();
        MapWrap sectionModule = new MapWrap();
        modules.add(sectionModule);
        sectionModule.put("type", "section");
        sectionModule.put("text.type", "plain-text");
        sectionModule.put("text.content", title);
        MapWrap imageModule = new MapWrap();
        modules.add(imageModule);
        cardImage.put("modules", modules);
        List<MapWrap> elements = Lists.newArrayList();
        imageModule.put("type", "container");
        imageModule.put("elements", elements);
        for (String s : src) {
            MapWrap element = new MapWrap();
            element.put("type", "image");
            element.put("src", s);
            elements.add(element);
        }
        return JacksonUtil.toFormatJson(Lists.newArrayList(cardImage));
    }

    private static String formatMessage(String message) {
        return message
                .replaceAll("[A-Za-z_@:]", "")
                .replaceAll("[<]{1}[\\d]+[>]{1}", "")
                .replaceAll("[ ]+", " ");
    }
}
