package cn.ivanzk.bot;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 跨平台转发时的文本清洗。
 */
@Component
public class MessageTextSanitizer {

    private static final Pattern MIRAI_TAG_PATTERN = Pattern.compile("<[\\d\\D]+?>");
    private static final Pattern KOOK_NOISE_PATTERN = Pattern.compile("[A-Za-z_@:]");
    private static final Pattern KOOK_MENTION_PATTERN = Pattern.compile("<\\d+>");
    private static final Pattern SPACES_PATTERN = Pattern.compile("[ ]+");

    public String forMirai(String message) {
        if (!StringUtils.hasText(message)) {
            return "";
        }
        return normalizeSpaces(MIRAI_TAG_PATTERN.matcher(message).replaceAll(""));
    }

    public String forKook(String message) {
        if (!StringUtils.hasText(message)) {
            return "";
        }
        String cleaned = KOOK_NOISE_PATTERN.matcher(message).replaceAll("");
        cleaned = KOOK_MENTION_PATTERN.matcher(cleaned).replaceAll("");
        return normalizeSpaces(cleaned);
    }

    private static String normalizeSpaces(String message) {
        return SPACES_PATTERN.matcher(message).replaceAll(" ").trim();
    }
}
