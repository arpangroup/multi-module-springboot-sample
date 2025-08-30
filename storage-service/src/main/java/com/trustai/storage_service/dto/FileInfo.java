package com.trustai.storage_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FileInfo {
    private String id;
    private int width;
    private int height;
    private long sizeInKb;
    private String extension;
    private String thumbnailUrl;
    private String downloadUrl;

    // Suggested additions
    private String originalFileName;
    private String mimeType;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private String path;
    private boolean isImage;
}

