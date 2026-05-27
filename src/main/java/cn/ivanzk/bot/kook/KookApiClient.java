package cn.ivanzk.bot.kook;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.ivanzk.config.CloudServerlessProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

/**
 * Kook HTTP API 客户端。
 */
@Component
@ConditionalOnProperty(prefix = "cloud-serverless.bots.kook", name = "enabled", havingValue = "true")
public class KookApiClient {

    private static final Logger log = LoggerFactory.getLogger(KookApiClient.class);
    private static final TypeReference<List<KookGuild>> GUILD_LIST = new TypeReference<>() {
    };
    private static final TypeReference<List<KookChannel>> CHANNEL_LIST = new TypeReference<>() {
    };

    private final CloudServerlessProperties properties;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    @Autowired
    public KookApiClient(CloudServerlessProperties properties, ObjectMapper objectMapper) {
        this(properties, objectMapper, RestClient.builder().build());
    }

    KookApiClient(CloudServerlessProperties properties, ObjectMapper objectMapper, RestClient restClient) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.restClient = restClient;
    }

    public List<KookGuild> listGuilds() {
        JsonNode data = getData(properties.getBots().getKook().getEndpoints().getGuildList());
        return readItems(data, GUILD_LIST);
    }

    public List<KookChannel> listChannels(String guildId) {
        if (!StringUtils.hasText(guildId)) {
            return Collections.emptyList();
        }
        JsonNode data = getData(properties.getBots().getKook().getEndpoints().getChannelList() + "?guild_id=" + guildId);
        return readItems(data, CHANNEL_LIST).stream()
                .filter(channel -> !channel.isCategory())
                .toList();
    }

    public void sendChannelMessage(String channelId, String content) {
        post(properties.getBots().getKook().getEndpoints().getChannelMessage(), Map.of(
                "target_id", channelId,
                "content", content
        ));
    }

    public void sendDirectMessage(String userId, String content) {
        post(properties.getBots().getKook().getEndpoints().getDirectMessage(), Map.of(
                "target_id", userId,
                "content", content
        ));
    }

    private JsonNode getData(String path) {
        try {
            JsonNode body = restClient.get()
                    .uri(url(path))
                    .header(HttpHeaders.AUTHORIZATION, authorization())
                    .retrieve()
                    .body(JsonNode.class);
            return body == null ? objectMapper.createObjectNode() : body.path("data");
        } catch (Exception e) {
            log.warn("Kook GET request failed: path={}, error={}", path, e.getMessage());
            return objectMapper.createObjectNode();
        }
    }

    private void post(String path, Map<String, Object> body) {
        try {
            restClient.post()
                    .uri(url(path))
                    .header(HttpHeaders.AUTHORIZATION, authorization())
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Kook POST request failed: path={}, target={}, error={}", path, body.get("target_id"), e.getMessage());
        }
    }

    private <T> List<T> readItems(JsonNode data, TypeReference<List<T>> type) {
        JsonNode items = data.path("items");
        if (!items.isArray()) {
            return Collections.emptyList();
        }
        return objectMapper.convertValue(items, type);
    }

    private String url(String path) {
        String baseUrl = properties.getBots().getKook().getBaseUrl();
        if (baseUrl.endsWith("/") && path.startsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1) + path;
        }
        if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
            return baseUrl + "/" + path;
        }
        return baseUrl + path;
    }

    private String authorization() {
        return "Bot " + properties.getBots().getKook().getToken();
    }
}
