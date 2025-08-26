package com.school.grade.common.result;

import lombok.Data;
import java.util.List;

/**
 * 分页响应类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
public class PageResult<T> {

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer current;

    /**
     * 每页大小
     */
    private Integer size;

    /**
     * 总页数
     */
    private Long pages;

    public PageResult() {
    }

    public PageResult(List<T> records, Long total, Integer current, Integer size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (total + size - 1) / size; // 计算总页数
    }

    /**
     * 是否有下一页
     */
    public boolean hasNext() {
        return current < pages;
    }

    /**
     * 是否有上一页
     */
    public boolean hasPrevious() {
        return current > 1;
    }

    /**
     * 是否为第一页
     */
    public boolean isFirst() {
        return current == 1;
    }

    /**
     * 是否为最后一页
     */
    public boolean isLast() {
        return current.longValue() >= pages;
    }
}