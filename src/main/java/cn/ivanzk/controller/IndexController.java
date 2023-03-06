package cn.ivanzk.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zk
 */
@RestController()
@RequestMapping("/")
public class IndexController {

    @RequestMapping(value = "/ping", method = {RequestMethod.GET, RequestMethod.POST})
    public String verify() {
        return "pong";
    }

}
