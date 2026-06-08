package org.example.beijing.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.UUID;

@Slf4j
@Component
public class FileUploadUtil {

    @Value("${upload.avatar-path:./uploads/avatars/}")
    private String avatarPath;

    @Value("${upload.image-path:./uploads/images/}")
    private String imagePath;

    @Value("${upload.video-path:./uploads/videos/}")
    private String videoPath;

    public String saveAvatar(MultipartFile file, Long userId) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("只能上传图片文件");
        }

        if (!avatarPath.endsWith(File.separator)) {
            avatarPath = avatarPath + File.separator;
        }

        String userDir = avatarPath + userId + File.separator;
        File dir = new File(userDir);
        if (!dir.exists() && !dir.mkdirs()) {
            log.error("创建目录失败: {}", dir.getAbsolutePath());
            throw new RuntimeException("无法创建上传目录: " + dir.getAbsolutePath());
        }

        File[] oldFiles = dir.listFiles((d, name) -> name.matches(".+\\.(jpg|jpeg|png|gif|bmp)$"));
        if (oldFiles != null) {
            for (File oldFile : oldFiles) {
                if (!oldFile.delete()) {
                    log.warn("删除旧头像文件失败: {}", oldFile.getAbsolutePath());
                } else {
                    log.info("已删除旧头像文件: {}", oldFile.getAbsolutePath());
                }
            }
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + suffix;

        Path filePath = Paths.get(userDir, filename);
        try {
            Files.copy(file.getInputStream(), filePath);
            log.info("文件保存成功: {}", filePath.toString());
        } catch (IOException e) {
            log.error("文件保存失败", e);
            throw new RuntimeException("文件上传失败");
        }

        String url = "/avatars/" + userId + "/" + filename;
        return url;
    }

    public String saveImage(MultipartFile file, String subDir) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("只能上传图片文件");
        }

        if (!imagePath.endsWith(File.separator)) {
            imagePath = imagePath + File.separator;
        }

        String targetDir = imagePath + subDir + File.separator;
        File dir = new File(targetDir);
        if (!dir.exists() && !dir.mkdirs()) {
            log.error("创建目录失败: {}", dir.getAbsolutePath());
            throw new RuntimeException("无法创建上传目录: " + dir.getAbsolutePath());
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + suffix;

        Path filePath = Paths.get(targetDir, filename);
        try {
            Files.copy(file.getInputStream(), filePath);
            log.info("文件保存成功: {}", filePath.toString());
        } catch (IOException e) {
            log.error("文件保存失败", e);
            throw new RuntimeException("文件上传失败");
        }

        String url = "/uploads/" + subDir + "/" + filename;
        return url;
    }

    public String saveVideo(MultipartFile file, String subDir) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new RuntimeException("只能上传视频文件");
        }

        if (!videoPath.endsWith(File.separator)) {
            videoPath = videoPath + File.separator;
        }

        String targetDir = videoPath + subDir + File.separator;
        File dir = new File(targetDir);
        if (!dir.exists() && !dir.mkdirs()) {
            log.error("创建目录失败: {}", dir.getAbsolutePath());
            throw new RuntimeException("无法创建上传目录: " + dir.getAbsolutePath());
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + suffix;

        Path filePath = Paths.get(targetDir, filename);
        try {
            Files.copy(file.getInputStream(), filePath);
            log.info("视频文件保存成功: {}", filePath.toString());
        } catch (IOException e) {
            log.error("文件保存失败", e);
            throw new RuntimeException("文件上传失败");
        }

        String url = "/videos/" + subDir + "/" + filename;
        return url;
    }

    public boolean deleteFileByUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }
        if (!fileUrl.startsWith("/uploads/") && !fileUrl.startsWith("/avatars/") && !fileUrl.startsWith("/videos/")) {
            return false;
        }

        String relativePath = fileUrl.substring(1);
        Path filePath;
        if (fileUrl.startsWith("/uploads/")) {
            String pathWithoutUploads = relativePath.substring("uploads/".length());
            filePath = Paths.get(imagePath, pathWithoutUploads);
        } else if (fileUrl.startsWith("/videos/")) {
            String pathWithoutVideos = relativePath.substring("videos/".length());
            filePath = Paths.get(videoPath, pathWithoutVideos);
        } else {
            String pathWithoutAvatars = relativePath.substring("avatars/".length());
            filePath = Paths.get(avatarPath, pathWithoutAvatars);
        }

        File file = filePath.toFile();
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                log.info("已删除文件: {}", filePath);
            } else {
                log.warn("删除文件失败: {}", filePath);
            }
            return deleted;
        }
        log.warn("文件不存在: {}", filePath);
        return false;
    }

    public void deleteUserAvatarFolder(Long userId) {
        String userDir = avatarPath + userId + File.separator;
        File dir = new File(userDir);
        if (dir.exists()) {
            try {
                Files.walk(dir.toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                log.info("已删除用户头像目录: {}", userDir);
            } catch (IOException e) {
                log.error("删除用户头像目录失败", e);
            }
        }
    }
}