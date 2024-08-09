package cn.ivanzk.cron;

import cn.ivanzk.config.quartz.AbstractQuartzJobHandler;
import cn.ivanzk.config.quartz.AnnotationQuartzCronJob;
import org.springframework.stereotype.Component;

@Component
@AnnotationQuartzCronJob("0 0/1 * * * ? ")//每1分钟执行一次
public class DataStatusRepair extends AbstractQuartzJobHandler {

    @Override
    public void onExecute() {
        System.out.println("DataStatusRepair");
    }
}
