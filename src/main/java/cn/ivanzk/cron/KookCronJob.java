package cn.ivanzk.cron;

import cn.ivanzk.config.kook.KookClient;
import cn.ivanzk.config.kook.KookProperties;
import com.google.common.collect.Lists;
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
import java.util.List;
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
    private static String lastPushUrl = null;

    /**
     * 心跳任务
     * 每天17点执行
     */
    @Scheduled(cron = "0 0 17 * * ?")
    public void heartBeat() {
        try {
            kookClient.directMessage(kookProperties.getAdmin(), "心跳");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询更新
     * 每5分钟执行一次
     */
//    @Scheduled(cron = "0 0/30 * * * ?")
    @Scheduled(cron = "0 0/5 * * * ?")
    public void updateMessage() {
        try {
            String url = "https://asia.archeage.com/news?lang=zh_TW";
            String html = httpClientProxy.doGetForResult(url, Maps.newHashMap());
            String pattern = "/news/{1}[\\d]+[?]{1}page={1}[\\d]+";
            Matcher matcher = Pattern.compile(pattern).matcher(html);

            List<String> awaitPushUrl = Lists.newArrayList();
            while (matcher.find()) {
                awaitPushUrl.add(0, matcher.group());
            }
            if (SmallTool.isEmpty(lastPushUrl)) {
                lastPushUrl = awaitPushUrl.get(awaitPushUrl.size() - 1);
                return;
            }
            int cutoff = 0;
            for (int i = 0; i < awaitPushUrl.size(); i++) {
                String s = awaitPushUrl.get(i);
                if (SmallTool.isEqual(lastPushUrl, s)) {
                    cutoff = i;
                    break;
                }
            }
            awaitPushUrl = awaitPushUrl.subList(cutoff + 1, awaitPushUrl.size());
            for (String newsUrl : awaitPushUrl) {
                url = "https://asia.archeage.com" + newsUrl;
                html = httpClientProxy.doGetForResult(url, Maps.newHashMap());
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
                        kookClient.channelImg("更新", imgUrl);
                    }
                }
                lastPushUrl = newsUrl;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("关闭无效连接:" + httpClientProxy.clearInvalidConnection());
    }
}
