package com.trustai.storage_service.controller;

import com.trustai.storage_service.dto.FileInfo;
import com.trustai.storage_service.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/images")
public class FileDownloadController {
    private final StorageService storageService;

    @Autowired
    public FileDownloadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/{fileId:.*}")
    public ResponseEntity<InputStreamResource> serveImage(@PathVariable String fileId) {
        try {
            InputStream imageStream = storageService.download(fileId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);  // adjust if you expect PNG, GIF, etc.

            return new ResponseEntity<>(new InputStreamResource(imageStream), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /*@GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            InputStream inputStream = storageService.download(filename);
            Resource resource = new InputStreamResource(inputStream);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }*/
}
