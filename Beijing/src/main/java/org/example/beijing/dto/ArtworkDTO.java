package org.example.beijing.dto;

import lombok.Data;

public class ArtworkDTO {

    @Data
    public static class PublishDTO {
        private String title;
        private String description;
        private String imageUrl;
    }
}