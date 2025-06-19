package org.schedx.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.schedx.persistence.SchedXTaskTrigger;
import org.springframework.scheduling.SchedulingAwareRunnable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>基于持久化策略的动态可变上下文</p>
 * <p>创建于 2025-06-15 21:57 21:57 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @see org.schedx.persistence.SchedXPersistence
 * @since 0.0.1
 */
@Slf4j
public class SchedXDynamicTaskContext {

    /**
     * 所有属性的承载容器，全局变量{@link #cron},{@link #zone},{@link #initialDelay},{@link #fixedDelay}和{@link #fixedRate}作为必须属性，将懒加载解析获得
     */
    protected final Map<String, Object> compatibleAttributes;

    @Getter
    protected final Runnable runnable;

    @Getter
    protected final Method method;

    @Getter
    protected final Object bean;

    @Setter
    protected StringValueResolver embeddedValueResolver;

    /**
     * {@link Scheduled#initialDelay()}和{@link Scheduled#initialDelayString()}的解析值
     */
    private final ValueSpec<Duration> initialDelay = ValueSpec.empty();

    /**
     * {@link Scheduled#cron()}的解析值
     */
    private final ValueSpec<String> cron = ValueSpec.empty();


    /**
     * {@link Scheduled#zone()}的解析值
     */
    private final ValueSpec<String> zone = ValueSpec.empty();


    /**
     * {@link Scheduled#fixedDelay()}和{@link Scheduled#fixedDelayString()}的解析值
     */
    private final ValueSpec<Duration> fixedDelay = ValueSpec.empty();

    /**
     * {@link Scheduled#fixedRate()}和{@link Scheduled#fixedRateString()}的解析值
     */
    private final ValueSpec<Duration> fixedRate = ValueSpec.empty();

    public SchedXDynamicTaskContext(Object bean, Method method, Runnable runnable, Map<String, Object> compatibleAttributes) {
        this.runnable = runnable;
        this.method = method;
        this.bean = bean;
        this.compatibleAttributes = compatibleAttributes;
    }

    @SuppressWarnings("unchecked")
    protected <R> R getAttributeValue(String name) {
        return (R) this.compatibleAttributes.get(name);
    }

    @SuppressWarnings("unchecked")
    protected <R> R getAttributeValue(String name, R defaultValue) {
        return (R) this.compatibleAttributes.getOrDefault(name, defaultValue);
    }

    /**
     * 获得{@link Scheduled#initialDelay()}和{@link Scheduled#initialDelayString()}的解析值
     *
     * @return {@code ValueSpec<Duration> }
     */
    public ValueSpec<Duration> getInitialDelay() {
        if (this.initialDelay.isResolved()) {
            return this.initialDelay;
        }

        final Duration value = this.parseInitialDelay();
        this.initialDelay.resolve(value);
        return this.initialDelay;
    }

    /**
     * 解析{@link Scheduled#initialDelay()}和{@link Scheduled#initialDelayString()}
     *
     * @return {@link Duration }
     */
    protected Duration parseInitialDelay() {
        // Determine initial delay
        Long initialDelayValue = this.getAttributeValue("initialDelay");
        Duration initialDelay = null;
        final TimeUnit timeUnit = this.getAttributeValue("timeUnit", TimeUnit.MILLISECONDS);
        if (initialDelayValue > 0) {
            initialDelay = toDuration(initialDelayValue, timeUnit);
        }
        String initialDelayString = this.getAttributeValue("initialDelayString");
        if (!StringUtils.hasText(initialDelayString)) {
            return initialDelay;
        }

        Assert.isNull(initialDelay, "Specify 'initialDelay' or 'initialDelayString', not both");

        if (this.embeddedValueResolver != null) {
            initialDelayString = this.embeddedValueResolver.resolveStringValue(initialDelayString);
        }
        if (!StringUtils.hasLength(initialDelayString)) {
            return null;
        }

        try {
            initialDelay = toDuration(initialDelayString, timeUnit);
            return initialDelay;
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException(
                    "Invalid initialDelayString value \"" + initialDelayString + "\"; " + ex);
        }
    }

    /**
     * 初始化或获得{@link #cron}
     *
     * @return {@code ValueSpec<String> }
     */
    public ValueSpec<String> getCron() {
        if (this.cron.isResolved()) {
            return this.cron;
        }

        final String value = this.parseCron();
        this.cron.resolve(value);
        return this.cron;
    }

    /**
     * 解析{@link Scheduled#cron()}
     *
     * @return {@link String }
     */
    protected String parseCron() {
        String cron = this.getAttributeValue("cron");
        if (!StringUtils.hasText(cron)) {
            return null;
        }
        if (this.embeddedValueResolver != null) {
            cron = this.embeddedValueResolver.resolveStringValue(cron);
        }
        if (!StringUtils.hasLength(cron)) {
            return null;
        }

        Assert.isTrue(this.getInitialDelay().isNullValue(), "'initialDelay' not supported for cron triggers");

        return cron;
    }

    /**
     * 初始化或获得{@link #zone}
     *
     * @return {@code ValueSpec<String> }
     */
    public ValueSpec<String> getZone() {
        if (this.zone.isResolved()) {
            return this.zone;
        }
        final String value = this.parseZone();
        this.zone.resolve(value);
        return this.zone;
    }

    /**
     * 解析{@link Scheduled#zone()}
     *
     * @return {@link String }
     */
    protected String parseZone() {
        if (this.getCron().isUnresolved()) {
            return null;
        }

        String zone = this.getAttributeValue("zone");
        if (this.embeddedValueResolver != null) {
            zone = this.embeddedValueResolver.resolveStringValue(zone);
        }

        Assert.isTrue(this.getInitialDelay().isUnresolved(), "'initialDelay' not supported for cron triggers");

        return zone;
    }

    /**
     * 初始化或获得{@link #fixedDelay}
     *
     * @return {@code ValueSpec<Duration> }
     */
    public ValueSpec<Duration> getFixedDelay() {
        if (this.fixedDelay.isResolved()) {
            return this.fixedDelay;
        }
        final Duration value = this.parseFixDelay();
        this.fixedDelay.resolve(value);
        return this.fixedDelay;
    }

    /**
     * 解析{@link Scheduled#fixedDelay()}和{@link Scheduled#fixedDelayString()}
     *
     * @return {@link Duration }
     */
    protected Duration parseFixDelay() {
        final TimeUnit timeUnit = this.getAttributeValue("timeUnit", TimeUnit.MILLISECONDS);
        // Check fixed delay
        Duration fixedDelay = null;
        final Long value = this.getAttributeValue("fixedDelay");
        if (null != value && value > 0) {
            fixedDelay = toDuration(value, timeUnit);
        }

        String fixedDelayString = this.getAttributeValue("fixedDelayString");
        if (!StringUtils.hasText(fixedDelayString)) {
            return fixedDelay;
        }
        if (this.embeddedValueResolver != null) {
            fixedDelayString = this.embeddedValueResolver.resolveStringValue(fixedDelayString);
        }

        if (!StringUtils.hasLength(fixedDelayString)) {
            return fixedDelay;
        }

        try {
            return this.toDuration(fixedDelayString, timeUnit);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Invalid fixedDelayString value \"" + fixedDelayString + "\"", ex);
        }
    }

    /**
     * 初始化或获得{@link #fixedRate}
     *
     * @return {@code ValueSpec<Duration> }
     */
    public ValueSpec<Duration> getFixedRate() {
        if (this.fixedRate.isResolved()) {
            return this.fixedRate;
        }
        final Duration value = this.parseFixedRate();
        this.fixedRate.resolve(value);
        return this.fixedRate;
    }

    /**
     * 解析{@link Scheduled#fixedRate()}和{@link Scheduled#fixedRateString()}
     *
     * @return {@link Duration }
     */
    protected Duration parseFixedRate() {
        // Check fixed rate
        Duration fixedRate = null;
        final Long value = this.getAttributeValue("fixedRate");
        TimeUnit timeUnit = this.getAttributeValue("timeUnit", TimeUnit.MILLISECONDS);
        if (null != value && value > 0) {
            fixedRate = toDuration(value, timeUnit);
        }

        String fixedRateString = this.getAttributeValue("fixedRateString");
        if (!StringUtils.hasText(fixedRateString)) {
            return fixedRate;
        }

        if (this.embeddedValueResolver != null) {
            fixedRateString = this.embeddedValueResolver.resolveStringValue(fixedRateString);
        }
        if (!StringUtils.hasLength(fixedRateString)) {
            return fixedRate;
        }
        try {
            return toDuration(fixedRateString, timeUnit);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException(
                    "Invalid fixedRateString value \"" + fixedRateString + "\"", ex);
        }
    }

    protected Duration toDuration(long value, TimeUnit timeUnit) {
        if (value < 0) {
            return null;
        }
        try {
            return Duration.ofNanos(timeUnit.toNanos(value));
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                    "Unsupported unit " + timeUnit + " for value \"" + value + "\": " + ex.getMessage());
        }
    }

    protected Duration toDuration(String value, TimeUnit timeUnit) {
        if (this.isDurationString(value)) {
            return Duration.parse(value);
        }
        return this.toDuration(Long.parseLong(value), timeUnit);
    }

    protected boolean isDurationString(String value) {
        return (value.length() > 1 && (isP(value.charAt(0)) || isP(value.charAt(1))));
    }

    private boolean isP(char ch) {
        return (ch == 'P' || ch == 'p');
    }

    public static SchedXDynamicTaskContext fromSchedXTask(SchedXTaskTrigger<?> trigger, ScheduledMethodRunnable runnable) {
        final Map<String, Object> compatibleAttributes = new HashMap<>();
        compatibleAttributes.put("timeUnit", trigger.getUnit());
        compatibleAttributes.put("zone", trigger.getZone());
        compatibleAttributes.put("fixedDelay", trigger.getFixedDelay());
        compatibleAttributes.put("fixedRate", trigger.getFixedRate());
        compatibleAttributes.put("cron", trigger.getCron());
        compatibleAttributes.put("initialDelay", trigger.getInitialDelay());
        return new SchedXDynamicTaskContext(runnable.getTarget(), runnable.getMethod(), runnable, compatibleAttributes);
    }
}