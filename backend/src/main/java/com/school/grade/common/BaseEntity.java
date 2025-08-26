package com.school.grade.common;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 实体基类，包含通用字段
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
     * 创建人
     */
    @CreatedBy
    @Column(name = "create_by", updatable = false, length = 50)
    private String createBy;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    @LastModifiedBy
    @Column(name = "update_by", length = 50)
    private String updateBy;

    /**
     * 删除标记 0:未删除 1:已删除
     */
    @Column(name = "deleted", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    private Integer deleted = 0;

    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;
}