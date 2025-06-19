package org.schedx.util;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * <p>集合工具类</p>
 * <p>创建于 2025-06-12 22:21 22:21 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public class CollectionUtil extends CollectionUtils {

    protected CollectionUtil() {
    }

    /**
     * 返回空{@link ArrayList}
     *
     * @return {@code List<T>}
     */
    public static <T> List<T> emptyArrayList() {
        return new ArrayList<>(0);
    }

    /**
     * 返回空{@link LinkedHashSet}
     *
     * @return {@code Set<T>}
     */
    public static <T> Set<T> emptyLinkedHashSet() {
        return new LinkedHashSet<>(0);
    }

    /**
     * 将{@link Collection}转为{@link List}
     *
     * @param c c
     * @return {@code List<T> }
     */
    public static <T> List<T> toList(Collection<T> c) {
        if (isEmpty(c)) {
            return emptyArrayList();
        }

        if (c instanceof List) {
            return (List<T>) c;
        }

        return new ArrayList<>(c);
    }

    /**
     * 将{@link Collection}转为{@link Set}
     *
     * @param c c
     * @return {@code Set<T> }
     */
    public static <T> Set<T> toSet(Collection<T> c) {
        if (isEmpty(c)) {
            return emptyLinkedHashSet();
        }

        if (c instanceof Set) {
            return (Set<T>) c;
        }

        return new LinkedHashSet<>(c);
    }

    /**
     * 返回第一个被{@code filter}命中的元素，否则返回{@code null}
     *
     * @param c      c
     * @param filter 筛选
     * @return {@link T }
     * @see #findFirstMatch(Collection, Collection)
     */
    public static <T> T findFirstMatch(Collection<T> c, Predicate<T> filter) {
        if (isEmpty(c)) {
            return null;
        }

        for (T t : c) {
            if (filter.test(t)) {
                return t;
            }
        }
        return null;
    }
}