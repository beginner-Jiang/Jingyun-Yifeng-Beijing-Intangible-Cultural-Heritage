package org.example.beijing.config;

import org.example.beijing.util.ResponseResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseResult<String> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        return ResponseResult.error(400, "文件过大，请上传小于10MB的图片或视频");
    }
}