package cn.ivanzk.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.net.comn.http.HttpClientProxy;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理html的工具类
 */
public class HtmlParseUtil {
    private static HttpClientProxy httpClientProxy = new HttpClientProxy();

    public static String doGetForRegexString(String url, String pattern) {
        System.out.println("关闭无效连接:" + httpClientProxy.clearInvalidConnection());
        try {
            String html = httpClientProxy.doGetForResult(url, Maps.newHashMap());
            Matcher matcher = Pattern.compile(pattern).matcher(html);
            while (matcher.find()) {
                return matcher.group();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> doGetForRegexStringList(String url, String pattern) {
        System.out.println("关闭无效连接:" + httpClientProxy.clearInvalidConnection());
        try {
            String html = httpClientProxy.doGetForResult(url, Maps.newHashMap());
            Matcher matcher = Pattern.compile(pattern).matcher(html);

            List<String> list = Lists.newArrayList();
            while (matcher.find()) {
                list.add(0, matcher.group());
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
