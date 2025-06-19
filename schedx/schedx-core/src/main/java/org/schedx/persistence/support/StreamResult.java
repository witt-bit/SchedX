package org.schedx.persistence.support;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

/**
 * <p>流式结果</p>
 * <p>创建于 2025-05-11 22:45 22:45 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public class StreamResult<R> implements Serializable {
    private static final long serialVersionUID = 9157578171379265139L;

    @Setter
    @Getter
    private List<R> result;

    private boolean hasMore;

    @Getter
    private long streamNo = 1;

    public boolean hasMore() {
        return this.hasMore;
    }

    public StreamResult(List<R> result, boolean hasMore) {
        this.result = result;
        this.hasMore = hasMore;
    }

    public StreamResult<R> next(List<R> nextResult, boolean isEnd) {
        this.result = nextResult;
        this.hasMore = !isEnd;
        return this;
    }

    public Stream<R> stream() {
        return this.result.stream();
    }
}