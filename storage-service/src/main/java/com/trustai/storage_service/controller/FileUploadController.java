package com.trustai.storage_service.controller;

import com.trustai.storage_service.dto.FileInfo;
import com.trustai.storage_service.service.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
public class FileUploadController {
    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping
    public ResponseEntity<List<FileInfo>> listFiles() {
        List<FileInfo> files = storageService.listAllFiles();
        return ResponseEntity.ok(files);
    }

    @PostMapping("/upload-multiple")
    public ResponseEntity<Map<String, Object>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, HttpServletRequest request) {
        Map<String, Object> uploadedFiles = new HashMap<>();

        for (MultipartFile file : files) {
            try {
                Thread.sleep(2000);
                FileInfo fileInfo = storageService.upload(file);
                //uploadedFiles.put(file.getOriginalFilename(), fileId);
                uploadedFiles.put(fileInfo.getId(), fileInfo);
            } catch (Exception e) {
                uploadedFiles.put(file.getOriginalFilename(), "Failed: " + e.getMessage());
            }
        }

        return ResponseEntity.ok(uploadedFiles);
    }
}
