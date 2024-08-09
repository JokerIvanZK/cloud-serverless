package cn.ivanzk.config.quartz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Cron定时任务处理注解
 *
 * @author 周彦平
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationQuartzCronJob {
    /**
     * cronExpression
     */
    public String value();

    /**
     * Job名称
     */
    public String jobName() default "";

    /**
     * 开始时间
     */
    public String startTime() default "";

    /**
     * 结束时间
     */
    public String endTime() default "";
}
