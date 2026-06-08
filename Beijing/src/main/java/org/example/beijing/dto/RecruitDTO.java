package org.example.beijing.dto;

import lombok.Data;

public class RecruitDTO {

    @Data
    public static class AddPost {
        private String title;
        private String description;
        private String inheritorName;
        private Integer maxApplicants; // 新增
    }

    @Data
    public static class Apply {
        private Long recruitId;
    }

    @Data
    public static class End {
        private Long recruitId;
    }
}