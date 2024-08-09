package cn.ivanzk.config.quartz;

import com.java.comn.thread.LastStatePeacekeepMember;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 定时任务Job处理抽象
 *
 * @author 周彦平
 */
public abstract class AbstractQuartzJobHandler implements Job {
    /**
     * 执行线程
     */
    private final LastStatePeacekeepMember<Boolean> peacekeepMember = new LastStatePeacekeepMember<Boolean>() {
        @Override
        public void doExecute(Boolean arg0) {
            try {
                AbstractQuartzJobHandler.this.onExecute();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 执行处理
     */
    public abstract void onExecute() throws Throwable;

    /**
     * 打印系统信息
     */
    public void systemOut(String content) {
        System.out.println(content);
    }

    /**
     * 打印错误信息
     */
    public void systemErr(String content) {
        System.err.println(content);
    }

    /**
     * 获取处理器
     */
    public LastStatePeacekeepMember<Boolean> getPeacekeepMember() {
        return peacekeepMember;
    }

    /**
     * 执行
     */
    public final void execute() {
        getPeacekeepMember().execute();
    }

    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException {
        getPeacekeepMember().execute();
    }
}
