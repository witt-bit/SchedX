package org.schedx.persistence;

import lombok.Getter;
import lombok.Setter;
import org.schedx.SchedXValueException;
import org.schedx.util.ObjectUtil;
import org.schedx.util.StringUtil;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

/**
 * <p>  </p>
 * <p>创建于 2025-06-14 20:51 20:51 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
@Getter
@Setter
public abstract class SchedXTaskTrigger<T extends SchedXTaskTrigger<T>> {

    /**
     * cron表达式,需要符合Spring cron规范, 同时支持{@code ${...}}表达式和Spring EL上下文表达式
     *
     * @see Scheduled#cron()
     * @see org.springframework.scheduling.support.CronExpression
     * @see org.springframework.util.StringValueResolver
     */
    protected String cron;

    /**
     * {@link #cron}所使用的时区，默认为{@code null}，表示使用系统默认时区，同时支持{@code ${...}}表达式和Spring EL上下文表达式
     *
     * @see Scheduled#zone()
     * @see java.util.TimeZone
     * @see org.springframework.util.StringValueResolver
     */
    protected String zone;

    /**
     * 第一次执行任务之前延迟的事件数量，单位可通过{@link #unit}调整,同时支持{@code ${...}}表达式和Spring EL上下文表达式
     *
     * @see Scheduled#initialDelay()
     * @see Scheduled#initialDelayString()
     */
    protected String initialDelay;

    /**
     * 固定间间隔，单位可通过{@link #unit}调整,同时支持{@code ${...}}表达式和Spring EL上下文表达式
     *
     * @see Scheduled#fixedDelay()
     * @see Scheduled#fixedDelayString()
     */
    protected String fixedDelay;

    /**
     * 固定速率,单位可通过{@link #unit}调整,同时支持{@code ${...}}表达式和Spring EL上下文表达式
     *
     * @see Scheduled#fixedRate()
     * @see Scheduled#fixedRateString()
     */
    protected String fixedRate;


    /**
     * 时间单位，默认为{@link TimeUnit#MILLISECONDS}，本次所有时间单位都使用此值
     *
     * @see Scheduled#timeUnit()
     */
    protected TimeUnit unit = TimeUnit.NANOSECONDS;

    /**
     * 调度类型
     * <ul>
     *     <li>0: 默认，cron表达式类型；需赋值{@link #cron}和{@link #zone}</li>
     *     <li>1: 固定延迟时间类型；需赋值{@link #fixedDelay}和{@link #unit}</li>
     *     <li>2: 固定间隔时间类型；需赋值{@link #fixedRate}和{@link #unit}</li>
     *     <li>3: 一次性执行类型；需赋值{@link #initialDelay}和{@link #unit}</li>
     * </ul>
     */
    protected int scheduleType = 0;

    /**
     * 构造{@link Scheduled#cron()}任务
     *
     * @param cron cron表达式
     * @param zone 时区
     * @return {@link T }
     * @see org.springframework.scheduling.config.CronTask
     * @see org.springframework.scheduling.support.CronTrigger
     * @see org.springframework.scheduling.support.CronExpression
     */
    @SuppressWarnings("unchecked")
    public T withCron(String cron, @Nullable String zone) {
        if (StringUtil.hasText(cron)) {
            throw SchedXValueException.missingValue("cron");
        }
        this.scheduleType = 0;
        this.cron = cron;
        this.zone = zone;

        // clean other fields
        this.fixedDelay = null;
        this.fixedRate = null;
        this.initialDelay = null;
        this.unit = null;
        return (T) this;
    }

    /**
     * 构造{@link Scheduled#fixedDelay()}任务
     *
     * @param initialDelay 初始延迟
     * @param fixedDelay   固定延迟
     * @param unit         单位
     * @return {@link T }
     * @see org.springframework.scheduling.config.FixedDelayTask
     * @see java.time.Duration
     */
    @SuppressWarnings("unchecked")
    public T withFixedDelay(String initialDelay, String fixedDelay, TimeUnit unit) {
        if (StringUtil.hasText(fixedDelay)) {
            throw SchedXValueException.missingValue("fixedDelay");
        }
        this.scheduleType = 1;
        this.initialDelay = ObjectUtil.defaultIfNull(initialDelay, "0");
        this.fixedDelay = fixedDelay;
        this.unit = ObjectUtil.defaultIfNull(unit, TimeUnit.MILLISECONDS);


        // clean other fields
        this.fixedRate = null;
        this.cron = null;
        this.zone = null;
        return (T) this;
    }

    /**
     * 构造{@link Scheduled#fixedRate()}任务
     *
     * @param initialDelay 初始延迟
     * @param fixedRate    固定速率
     * @param unit         单位
     * @return {@link T }
     * @see org.springframework.scheduling.config.FixedRateTask
     * @see java.time.Duration
     */
    @SuppressWarnings("unchecked")
    public T withFixedRate(String initialDelay, String fixedRate, TimeUnit unit) {
        if (StringUtil.hasText(fixedRate)) {
            throw SchedXValueException.missingValue("fixedRate");
        }
        this.scheduleType = 2;
        this.initialDelay = ObjectUtil.defaultIfNull(initialDelay, "0");
        this.fixedRate = fixedRate;
        this.unit = ObjectUtil.defaultIfNull(unit, TimeUnit.MILLISECONDS);


        // clean other fields
        this.fixedDelay = null;
        this.cron = null;
        this.zone = null;
        return (T) this;
    }

    /**
     * 构造单次执行任务, 参照{@code org.springframework.scheduling.config.OneTimeTask},仅Spring 6.1后支持
     *
     * @param initialDelay 初始延迟
     * @param unit         单位
     * @return {@link T }
     */
    @SuppressWarnings("unchecked")
    public T withOneTime(@Nullable String initialDelay, @Nullable TimeUnit unit) {
        this.scheduleType = 3;
        this.initialDelay = ObjectUtil.defaultIfNull(initialDelay, "0");
        this.unit = ObjectUtil.defaultIfNull(unit, TimeUnit.MILLISECONDS);


        // clean other fields
        this.fixedDelay = null;
        this.fixedRate = null;
        this.cron = null;
        this.zone = null;
        return (T) this;
    }

    /**
     * 构造单次执行任务, 参照{@code org.springframework.scheduling.config.OneTimeTask},仅Spring 6.1后支持
     *
     * @return {@link T }
     * 
     * @see #withOneTime(String, TimeUnit) 
     */
    public T withOneTime() {
        return this.withOneTime(null, null);
    }

}