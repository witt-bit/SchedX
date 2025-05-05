package org.schedx.parser;

import java.util.function.BiFunction;

/**
 * <p>方法参数解析器</p>
 * <p>创建于 2025-05-04 19:59 19:59 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public interface MethodArgumentParser<R> extends BiFunction<String, MethodArgument, R> {

    @Override
    default R apply(String s, MethodArgument methodArgument) {
        return this.parse(s, methodArgument);
    }

    /**
     * 解析
     *
     * @param paramString 参数字符串
     * @return {@link R }
     */
    R parse(String paramString, MethodArgument methodArgument);
}