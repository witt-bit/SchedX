package org.schedx.parser;

/**
 * <p>无法反序列化的类解析实现</p>
 * <p>创建于 2025-05-04 20:14 20:14 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public class NoDeserializeMethodArgumentParser implements MethodArgumentParser<Object> {

    public static final NoDeserializeMethodArgumentParser INSTANCE = new NoDeserializeMethodArgumentParser();

    private NoDeserializeMethodArgumentParser() {
    }


    @Override
    public Object parse(String paramString, MethodArgument methodArgument) {
        return null;
    }
}