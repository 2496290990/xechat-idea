package cn.xeblog.plugin.game.dld.model.common;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author eleven
 * @date 2023/10/13 10:24
 * @apiNote
 */
public class Page<T> {
    private static final long serialVersionUID = 8545996863226528798L;
    private List<T> records;
    private Long total;
    private Long size;
    private Long current;
    private List<OrderItem> orders;

    public Page() {
        this.records = Collections.emptyList();
        this.total = 0L;
        this.size = 10L;
        this.current = 1L;
        this.orders = new ArrayList();
    }

    public Page(long current, long size) {
        this(current, size, 0L);
    }

    public Page(long current, long size, long total) {
        this(current, size, total, true);
    }

    public Page(long current, long size, boolean searchCount) {
        this(current, size, 0L, searchCount);
    }

    public Page(long current, long size, long total, boolean searchCount) {
        this.records = Collections.emptyList();
        this.total = 0L;
        this.size = 10L;
        this.current = 1L;
        this.orders = new ArrayList();
        if (current > 1L) {
            this.current = current;
        }

        this.size = size;
        this.total = total;
    }

    public boolean hasPrevious() {
        return this.current > 1L;
    }

    public boolean hasNext() {
        return this.current < this.getPages();
    }

    public List<T> getRecords() {
        return this.records;
    }

    public Page<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    public long getTotal() {
        return this.total;
    }

    public Page<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    public long getSize() {
        return this.size;
    }

    public Page<T> setSize(long size) {
        this.size = size;
        return this;
    }

    public long getCurrent() {
        return this.current;
    }

    public Page<T> setCurrent(long current) {
        this.current = current;
        return this;
    }

    public Page<T> addOrder(OrderItem... items) {
        this.orders.addAll(Arrays.asList(items));
        return this;
    }

    public Page<T> addOrder(List<OrderItem> items) {
        this.orders.addAll(items);
        return this;
    }

    public List<OrderItem> orders() {
        return this.orders;
    }

    public long getPages() {
        return getTotal() / getSize();
    }
}
