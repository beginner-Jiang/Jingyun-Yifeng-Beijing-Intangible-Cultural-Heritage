package org.example.beijing.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.beijing.util.FileUploadUtil;
import org.example.beijing.util.ResponseResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final FileUploadUtil fileUploadUtil;

    @PostMapping("/image")
    public ResponseResult<String> uploadImage(@RequestParam("file") MultipartFile file,
                                              @RequestParam("type") String type) {
        if (file.isEmpty()) {
            return ResponseResult.badRequest("文件不能为空");
        }
        try {
            String url = fileUploadUtil.saveImage(file, type);
            return ResponseResult.success(url);
        } catch (Exception e) {
            log.error("图片上传失败", e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @PostMapping("/video")
    public ResponseResult<String> uploadVideo(@RequestParam("file") MultipartFile file,
                                              @RequestParam("type") String type) {
        if (file.isEmpty()) {
            return ResponseResult.badRequest("文件不能为空");
        }
        // 限制视频文件大小（50MB）
        if (file.getSize() > 50 * 1024 * 1024) {
            return ResponseResult.badRequest("视频文件过大，请小于50MB");
        }
        try {
            String url = fileUploadUtil.saveVideo(file, type);
            return ResponseResult.success(url);
        } catch (Exception e) {
            log.error("视频上传失败", e);
            return ResponseResult.error(e.getMessage());
        }
    }
}