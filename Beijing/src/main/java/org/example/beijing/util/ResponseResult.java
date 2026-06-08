package org.example.beijing.util;

import lombok.Data;

/**
 * 统一响应结果封装
 */
@Data
public class ResponseResult<T> {
    private Integer code;    // 状态码：200成功，其他失败
    private String msg;      // 提示信息
    private T data;          // 返回数据

    public ResponseResult() {
    }

    public ResponseResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(200, "success", null);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(200, "success", data);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> ResponseResult<T> success(String msg, T data) {
        return new ResponseResult<>(200, msg, data);
    }

    /**
     * 失败响应（默认500）
     */
    public static <T> ResponseResult<T> error(String msg) {
        return new ResponseResult<>(500, msg, null);
    }

    /**
     * 失败响应（自定义状态码）
     */
    public static <T> ResponseResult<T> error(Integer code, String msg) {
        return new ResponseResult<>(code, msg, null);
    }

    /**
     * 参数错误（400）
     */
    public static <T> ResponseResult<T> badRequest(String msg) {
        return new ResponseResult<>(400, msg, null);
    }

    /**
     * 未授权（401）
     */
    public static <T> ResponseResult<T> unauthorized(String msg) {
        return new ResponseResult<>(401, msg, null);
    }

    /**
     * 禁止访问（403）
     */
    public static <T> ResponseResult<T> forbidden(String msg) {
        return new ResponseResult<>(403, msg, null);
    }

    /**
     * 资源不存在（404）
     */
    public static <T> ResponseResult<T> notFound(String msg) {
        return new ResponseResult<>(404, msg, null);
    }
}