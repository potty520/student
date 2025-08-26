package com.school.grade.common;

import lombok.Data;

/**
 * 通用API响应类
 * 
 * @author Qoder
 * @version 1.0.0
 */
@Data
public class ApiResult<T> {

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 成功响应码
     */
    public static final Integer SUCCESS_CODE = 200;

    /**
     * 失败响应码
     */
    public static final Integer ERROR_CODE = 500;

    public ApiResult() {
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResult<T> success() {
        return new ApiResult<>(SUCCESS_CODE, "操作成功", null);
    }

    /**
     * 成功响应（有数据）
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(SUCCESS_CODE, "操作成功", data);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(SUCCESS_CODE, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> ApiResult<T> error(String message) {
        return new ApiResult<>(ERROR_CODE, message, null);
    }

    /**
     * 失败响应（自定义响应码）
     */
    public static <T> ApiResult<T> error(Integer code, String message) {
        return new ApiResult<>(code, message, null);
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return SUCCESS_CODE.equals(this.code);
    }
}