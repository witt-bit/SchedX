package org.schedx.parser;

/**
 * <p>{@link String}类型解析器</p>
 * <p>创建于 2025-05-04 20:07 20:07 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public class StringMethodArgumentParser implements MethodArgumentParser<String>{
    @Override
    public String parse(String paramString, MethodArgument methodArgument) {
        return paramString;
    }
}