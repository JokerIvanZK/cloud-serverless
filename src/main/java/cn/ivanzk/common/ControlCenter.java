package cn.ivanzk.common;

import cn.ivanzk.config.quartz.AbstractQuartzJobHandler;
import cn.ivanzk.config.quartz.AnnotationQuartzCalendarJob;
import cn.ivanzk.config.quartz.AnnotationQuartzCronJob;
import com.java.comn.exception.MsgException;
import com.java.comn.util.ClassUtil;
import com.java.comn.util.SmallTool;
import com.net.comn.http.HttpClientProxy;
import com.net.comn.quartz.QuartzProxy;
import com.net.comn.server.ServerContext;
import org.quartz.Job;

import java.util.Date;

/**
 * 控制中心
 *
 * @author 周彦平
 */
public class ControlCenter {
    //Http客户端
    public static final HttpClientProxy httpClient = new HttpClientProxy();
    /**
     * 定时任务代理
     */
    private static QuartzProxy quartzProxy = new QuartzProxy() {
        @Override
        public Job getJobInstance(Class<? extends Job> jobClass) {
            if (jobClass == null) return null;
            return ServerContext.getBean(jobClass);
        }
//        public Job getJobInstance(JobDetail jobDetail) {
//            Job job = super.getJobInstance(jobDetail.getJobClass());
//            if (job != null) return job;
//            Class<? extends Job> jobClass = jobDetail.getJobClass();
//            if (jobClass == null) return null;
//            job = ServerContext.getBean(jobClass);
//            return job;
//        }
    };

    /**
     * 初始化定时任务处理器
     */
    public static void initCrontabHandler() {
        for (Class<AbstractQuartzJobHandler> clazz : ClassUtil.getPackageClass(AbstractQuartzJobHandler.class, "cn.ivanzk.cron")) {
            try {
                AnnotationQuartzCronJob annotationQuartzCronJob = clazz.getAnnotation(AnnotationQuartzCronJob.class);
                if (annotationQuartzCronJob != null && !SmallTool.isEmpty(annotationQuartzCronJob.value())) {
                    Date startTime = SmallTool.toDate(annotationQuartzCronJob.startTime(), null);
                    Date endTime = SmallTool.toDate(annotationQuartzCronJob.endTime(), null);
                    if (quartzProxy.addCronJob(annotationQuartzCronJob.jobName(), clazz, startTime, endTime, annotationQuartzCronJob.value()) != null) {
                        System.out.println(String.format("Quartz Cron Job Handler：%s - %s", annotationQuartzCronJob.value(), clazz.getName()));
                    }
                }
                AnnotationQuartzCalendarJob annotationQuartzCalendarJob = clazz.getAnnotation(AnnotationQuartzCalendarJob.class);
                if (annotationQuartzCalendarJob != null && annotationQuartzCalendarJob.interval() > 0 && annotationQuartzCalendarJob.unit() != null) {
                    Date startTime = SmallTool.toDate(annotationQuartzCalendarJob.startTime(), null);
                    Date endTime = SmallTool.toDate(annotationQuartzCalendarJob.endTime(), null);
                    if (quartzProxy.addCalendarJob(annotationQuartzCalendarJob.jobName(), clazz, startTime, endTime, annotationQuartzCalendarJob.interval(), annotationQuartzCalendarJob.unit()) != null) {
                        System.out.println(String.format("Quartz Calendar Job Handler：%s - %s - %s", annotationQuartzCalendarJob.interval(), annotationQuartzCalendarJob.unit(), clazz.getName()));
                    }
                }
            } catch (MsgException e) {
                System.err.println(e.question());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 终端缓存
     */
//    public static DataCache<Long, Carrier> carrierCache = new DataCache<Long, Carrier>(10 * 60 * 1000L) {
//        @Override
//        public Carrier query(Long arg0) {
//            return carrierServiceGet.get().get(arg0);
//        }
//    };
}
