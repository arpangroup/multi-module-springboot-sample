package com.trustai.common.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


public interface FileUploadApi {
    ResponseEntity<Map<String, Object>> uploadMultipleFiles(@RequestPart("files") MultipartFile[] files);

    public String uploadFile(MultipartFile file) ;

}
