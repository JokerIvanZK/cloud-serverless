package cn.ivanzk.cron;

import cn.ivanzk.config.kook.KookClient;
import cn.ivanzk.config.kook.KookProperties;
import com.google.common.collect.Maps;
import com.java.comn.assist.MapWrap;
import com.java.comn.util.JacksonUtil;
import com.java.comn.util.SmallTool;
import com.net.comn.http.HttpClientProxy;
import gui.ava.html.parser.HtmlParser;
import gui.ava.html.parser.HtmlParserImpl;
import gui.ava.html.renderer.ImageRenderer;
import gui.ava.html.renderer.ImageRendererImpl;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mirai机器人定时任务
 *
 * @author zk
 */
@Component
@ConditionalOnBean(KookClient.class)
public class KookCronJob {
    @Autowired
    private KookClient kookClient;
    @Autowired
    private KookProperties kookProperties;

    private static HttpClientProxy httpClientProxy = new HttpClientProxy();
    private static String latestNewsUrl = null;

    /**
     * 查询更新
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void updateMessage() {
        try {
            String url = "https://asia.archeage.com/news?lang=zh_TW";
            Map<String, Object> header = Maps.newHashMap();
            header.put("Content-Language", "zh-TW");
            String html = httpClientProxy.doGetForResult(url, header);
            String pattern = "/news/{1}[\\d]+[?]{1}page={1}[\\d]+";
            Matcher matcher = Pattern.compile(pattern).matcher(html);
            if (matcher.find()) {
                String newsUrl = matcher.group();
                if (latestNewsUrl == null) {
                    latestNewsUrl = newsUrl;
                } else {
                    if (!SmallTool.isEqual(latestNewsUrl, newsUrl)) {
                        latestNewsUrl = newsUrl;
                        url = "https://asia.archeage.com" + newsUrl;
                        html = httpClientProxy.doGetForResult(url, header);
                        pattern = "<article class=\"view\">{1}[\\d\\D]+</article>{1}";
                        matcher = Pattern.compile(pattern).matcher(html);

                        if (matcher.find()) {
                            html = matcher.group();
                            HtmlParser htmlParser = new HtmlParserImpl();
                            htmlParser.loadHtml(html);
                            ImageRenderer imageRenderer = new ImageRendererImpl(htmlParser);
                            imageRenderer.saveImage("./1.png");

                            url = kookProperties.getBaseUrl() + kookProperties.getUploadFile();
                            CloseableHttpClient httpClient = HttpClients.createDefault();
                            HttpPost httpPost = new HttpPost(url);
                            httpPost.addHeader("Authorization", "Bot " + kookProperties.getToken());

                            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                            builder.addBinaryBody(
                                    "file",
                                    new FileInputStream("./1.png"),
                                    ContentType.MULTIPART_FORM_DATA,
                                    "1.png"
                            );
                            HttpEntity multipart = builder.build();
                            httpPost.setEntity(multipart);
                            CloseableHttpResponse response = httpClient.execute(httpPost);
                            HttpEntity responseEntity = response.getEntity();
                            String jsonString = EntityUtils.toString(responseEntity, "UTF-8");
                            MapWrap result = JacksonUtil.fromJson(jsonString, MapWrap.class);
                            String imgUrl = SmallTool.toString(result.get("data.url"), null);
                            if (SmallTool.notEmpty(imgUrl)) {
                                kookClient.sendImg("测试更新", imgUrl);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("心跳任务失败，原因：" + e.getMessage());
        }
    }
}
