package org.example.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>预定义测试的{@link Scheduled}项</p>
 * <p>创建于 2025-04-26 22:46 22:46 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
@Slf4j
@Component
public class DefineJobs {
    private static final Map<String, AtomicLong> RUNNING_COUNTER = new ConcurrentHashMap<>();

    /**
     * 使用cron表达式，每秒运行
     */
    @Scheduled(cron = "* * * * * *")
    public void everySecondsWithCron() {
        this.record();
    }

    // /**
    //  * 使用cron表达式，每秒运行
    //  */
    // @Scheduled(cron = "* * * * * *")
    // public void everySecondsWithCron(String params) {
    //     this.record();
    //     log.info("{} everySecondsWithCron",params);
    // }


    /**
     * 使用固定频率，每秒运行
     */
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    public void everySecondsWithFixedRate() {
        this.record();
    }

    /**
     * 获得调用者
     *
     * @return {@code String[]}, 调用类权限定名,方法名
     */
    private String[] whoCallMe() {
        final String fqcn = "whoCallMe";
        int index = -1;
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (index >= 0) {
                index += 1;
            }
            final String methodName = element.getMethodName();
            // 后移两位:getStackTrace->[whoCallMe]->record->(this)
            if (2 == index) {
                return new String[]{
                        element.getClassName(),
                        methodName
                };
            }
            // 定位栈开始点
            if (fqcn.equals(methodName)) {
                index = 0;
            }
        }
        return null;
    }


    /**
     * 记录运行函数和次数
     */
    private void record() {
        final String[] caller = this.whoCallMe();
        if (null == caller) {
            return;
        }
        String className = caller[0];
        String methodName = caller[1];
        AtomicLong counter = RUNNING_COUNTER.computeIfAbsent(className + "." + methodName, k -> new AtomicLong());
        final long current = counter.incrementAndGet();

        log.info("{} Running --> {} ...", methodName, current);
    }


    public void test() {

    }
}