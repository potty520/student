package com.school.grade.common;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 基础实体类，包含通用字段
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     */
    @Column(name = "create_by")
    private Long createBy;

    /**
     * 更新人ID
     */
    @Column(name = "update_by")
    private Long updateBy;

    /**
     * 删除标记 0:未删除 1:已删除
     */
    @Column(name = "deleted", columnDefinition = "TINYINT DEFAULT 0")
    private Integer deleted = 0;

    /**
     * 状态 0:禁用 1:启用
     */
    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;

    /**
     * 排序号
     */
    @Column(name = "sort_order", columnDefinition = "INT DEFAULT 0")
    private Integer sortOrder = 0;

    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;
}