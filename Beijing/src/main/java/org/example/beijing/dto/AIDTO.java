package org.example.beijing.dto;

import lombok.Data;

public class AIDTO {

    @Data
    public static class TextGenDTO {
        private String prompt;
    }

    @Data
    public static class ImageGenDTO {
        private String prompt;
        private String refImage;   // 参考图片URL
    }

    @Data
    public static class VideoGenDTO {
        private String prompt;
        private String refVideo;   // 参考视频URL
    }
}