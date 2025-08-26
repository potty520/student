package com.school.grade.common;

import lombok.Data;

/**
 * 分页查询参数
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
public class PageQuery {

    /**
     * 页码（从1开始）
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 排序字段
     */
    private String sort;

    /**
     * 排序方向 asc/desc
     */
    private String order = "desc";

    /**
     * 搜索关键字
     */
    private String keyword;

    /**
     * 获取偏移量
     */
    public Integer getOffset() {
        return (page - 1) * size;
    }

    /**
     * 获取页码号（为Service层兼容）
     */
    public Integer getPageNum() {
        return page;
    }

    /**
     * 获取页面大小（为Service层兼容）
     */
    public Integer getPageSize() {
        return size;
    }

    /**
     * 校验并设置默认值
     */
    public void validate() {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        if (size > 100) {
            size = 100; // 限制最大页面大小
        }
        if (order == null || (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc"))) {
            order = "desc";
        }
    }
}