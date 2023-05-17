package cn.ivanzk.cron;

import cn.ivanzk.config.kook.KookClient;
import cn.ivanzk.config.kook.KookProperties;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.java.comn.assist.MapWrap;
import com.java.comn.util.JacksonUtil;
import com.java.comn.util.SmallTool;
import com.net.comn.http.HttpClientProxy;
import com.net.comn.server.ServerContext;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
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
    private static final HttpClientProxy httpClientProxy = new HttpClientProxy();
    @Value("${lastPushUrl}")
    private String lastPushUrl = null;

    /**
     * 心跳任务
     * 每天17点执行
     */
    @Scheduled(cron = "0 0 17 * * ?")
    public void heartBeat() {
        KookClient kookClient = ServerContext.getBean(KookClient.class);
        KookProperties kookProperties = ServerContext.getBean(KookProperties.class);
        if (kookClient == null || kookProperties == null) {
            return;
        }
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
    @Scheduled(cron = "0 0/5 * * * ?")
    public void updateMessage() throws InterruptedException {
        System.out.println("关闭无效连接:" + httpClientProxy.clearInvalidConnection());
        KookClient kookClient = ServerContext.getBean(KookClient.class);
        KookProperties kookProperties = ServerContext.getBean(KookProperties.class);
        if (kookClient == null || kookProperties == null) {
            return;
        }

        List<String> imgUrls = Lists.newArrayList();
        HtmlParser htmlParser;
        ImageRenderer imageRenderer;
        for (String html : getHtmlString()) {
            htmlParser = new HtmlParserImpl();
            htmlParser.loadHtml(html);
            imageRenderer = new ImageRendererImpl(htmlParser);
            imageRenderer.saveImage("./1.png");
            imageRenderer.clearCache();

            String imgUrl = KookClient.uploadFile(kookProperties, "./1.png");
            if (SmallTool.notEmpty(imgUrl)) {
                imgUrls.add(imgUrl);
            }
            Thread.sleep(5000);
        }
        kookClient.channelImg("更新", imgUrls.toArray(new String[0]));
        Thread.sleep(1000 * 60);
        System.gc();
    }

    private List<String> getHtmlString() {
        List<String> list = Lists.newArrayList();

        String url = "https://asia.archeage.com/news?lang=zh_TW";
        String html = httpClientProxy.doGetTryForResult(url, Maps.newHashMap());
        String pattern = "/news/{1}[\\d]+[?]{1}page={1}[\\d]+";
        Matcher matcher = Pattern.compile(pattern).matcher(html);

        List<String> awaitPushUrl = Lists.newArrayList();
        while (matcher.find()) {
            awaitPushUrl.add(0, matcher.group());
        }
        if (SmallTool.isEmpty(lastPushUrl)) {
            lastPushUrl = awaitPushUrl.get(awaitPushUrl.size() - 1);
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

        System.out.println("==========================");
        System.out.println("上次推送的消息:" + lastPushUrl);
        for (String s : awaitPushUrl) {
            System.out.println("等待推送的消息:" + s);
        }
        System.out.println("==========================");

        for (String newsUrl : awaitPushUrl) {
            url = "https://asia.archeage.com" + newsUrl;
            html = httpClientProxy.doGetTryForResult(url, Maps.newHashMap());
            pattern = "<article class=\"view\">{1}[\\d\\D]+</article>{1}";
            matcher = Pattern.compile(pattern).matcher(html);

            if (matcher.find()) {
                html = matcher.group();
                list.add(html);
            }
            lastPushUrl = newsUrl;
        }
        return list;
    }
}