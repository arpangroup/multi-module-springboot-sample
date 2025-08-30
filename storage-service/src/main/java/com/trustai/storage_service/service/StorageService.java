package com.trustai.storage_service.service;

import com.trustai.storage_service.dto.FileInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface StorageService {
    List<FileInfo> listAllFiles();
    FileInfo getFile(String id);
    boolean isFileExist(String id);
    FileInfo upload(MultipartFile file);
    String upload(File file);
    InputStream  download(String fileId);
    void delete(String fileId);
}
