package com.trustai.storage_service.service.impl;

import com.trustai.storage_service.dto.FileInfo;
import com.trustai.storage_service.mapper.FileInfoMapper;
import com.trustai.storage_service.service.StorageService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service("localStorageService")
@RequiredArgsConstructor
public class LocalStorageService implements StorageService {
    private final Path root = Paths.get("uploads");
    private final FileInfoMapper mapper;

    @Autowired
    private HttpServletRequest request; // this will be request-scoped

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(root);
    }

    @Override
    public List<FileInfo> listAllFiles() {
        try {
            return Files.list(root)
                    .filter(Files::isRegularFile)
                    .map(path -> mapper.mapToFileInfo(path, request))
                    //.map(path -> baseUrl + DOWNLOAD_PATH + path.getFileName().toString())
                    //.map(path -> path.getFileName().toString())
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Could not list files", e);
        }
    }

    @Override
    public FileInfo getFile(String id) {
        Path filePath = root.resolve(id);
        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            return mapper.mapToFileInfo(filePath, request);
        } else {
            throw new RuntimeException("File not found: " + id);
        }
    }

    @Override
    public boolean isFileExist(String id) {
        Path filePath = root.resolve(id);
        return Files.exists(filePath) && Files.isRegularFile(filePath);
    }

    @Override
    public FileInfo upload(MultipartFile file) {
        try {
            Path destination = root.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            //return destination.toString();

            return mapper.mapToFileInfo(destination, request);
        } catch (IOException e) {
            throw new RuntimeException("Could not store the file", e);
        }
    }

    @Override
    public String upload(File file) {
        try {
            Path destination = root.resolve(file.getName());
            Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            return destination.toString();
        } catch (IOException e) {
            throw new RuntimeException("Could not store the file", e);
        }
    }

    @Override
    public InputStream  download(String fileId) {
        try {
            //return Files.readAllBytes(root.resolve(fileId));
            return Files.newInputStream(root.resolve(fileId));
        } catch (IOException e) {
            throw new RuntimeException("Could not read the file", e);
        }
    }

    @Override
    public void delete(String fileId) {
        try {
            Files.deleteIfExists(root.resolve(fileId));
        } catch (IOException e) {
            throw new RuntimeException("Could not delete the file", e);
        }
    }


}
