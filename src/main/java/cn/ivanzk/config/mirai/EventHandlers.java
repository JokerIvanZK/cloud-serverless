package cn.ivanzk.config.mirai;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 事件监听
 *
 * @author zk
 */
public class EventHandlers extends SimpleListenerHost {
    private static final Logger log = LoggerFactory.getLogger(EventHandlers.class);
    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception){
        // 处理事件处理时抛出的异常
        log.error("事件处理异常 => ", exception.getMessage());
    }
    @EventHandler
    public void onMessage(@NotNull MessageEvent event) throws Exception {
        System.out.println(event.getSender().getId());
        System.out.println(event.getMessage());
        // 无返回值, 表示一直监听事件.
    }
}
