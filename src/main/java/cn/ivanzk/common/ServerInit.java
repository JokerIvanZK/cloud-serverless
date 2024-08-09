package cn.ivanzk.common;

import cn.ivanzk.server.BasicService;
import com.java.comn.util.SmallTool;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zk
 */
@Service
public class ServerInit implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired(required = false)
    private List<BasicService> serverList;

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent contextRefreshedEvent) {
        init();
        ControlCenter.initCrontabHandler();
    }

    private void init() {
        if (SmallTool.isEmpty(serverList)) {
            return;
        }
        for (BasicService server : serverList) {
            try {
                server.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
