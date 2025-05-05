package org.schedx.parser;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>{@link MethodArgumentParser}注册器</p>
 * <p>创建于 2025-05-04 20:05 20:05 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public final class MethodArgumentParserRegistry {
    private static final Map<Type, MethodArgumentParser<?>> PARSERS = new ConcurrentHashMap<>();

    static {
        register(String.class, new StringMethodArgumentParser());
    }

    /**
     * 获得某个类的解析器
     *
     * @param type 类型
     * @return {@link MethodArgumentParser }<{@link T }>
     */
    @SuppressWarnings("unchecked")
    public static <T> MethodArgumentParser<T> getParser(Type type) {
        final MethodArgumentParser<?> parser = PARSERS.get(type);
        if (null != parser) {
            return (MethodArgumentParser<T>) parser;
        }

        return (MethodArgumentParser<T>) NoDeserializeMethodArgumentParser.INSTANCE;
    }

    /**
     * 注册解析器
     *
     * @param type   类型
     * @param parser 解析器
     */
    public static void register(Type type, MethodArgumentParser<?> parser) {
        if (null == type || null == parser) {
            return;
        }
        PARSERS.put(type, parser);
    }
}