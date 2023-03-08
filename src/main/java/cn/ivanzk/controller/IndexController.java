package cn.ivanzk.controller;

import com.google.common.collect.Maps;
import net.mamoe.mirai.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author zk
 */
@RestController()
@RequestMapping("/")
public class IndexController {
    @Autowired(required = false)
    private Bot bot;

    @GetMapping(value = "/ping")
    public String verify() {
        return "pong";
    }

    @GetMapping(value = "/state")
    public Map<String, Object> state() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("MiraiBot", bot.isOnline());
        return map;
    }
}
