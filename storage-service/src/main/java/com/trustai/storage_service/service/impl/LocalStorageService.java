package com.trustai.storage_service.service.impl;

import com.trustai.storage_service.dto.FileInfo;
import com.trustai.storage_service.mapper.FileInfoMapper;
import com.trustai.storage_service.service.StorageService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.stream.Stream;

@Service("localStorageService")
@RequiredArgsConstructor
@Slf4j
public class LocalStorageService implements StorageService {
    private final Path root = Paths.get("/app/uploads");
    private final FileInfoMapper mapper;

    @Autowired
    private HttpServletRequest request; // this will be request-scoped

    @PostConstruct
    public void init() throws IOException {
        log.info("Initializing local storage at path: {}", root);
        Files.createDirectories(root);
    }

    @Override
    public List<FileInfo> listAllFiles() {
        try {
            log.debug("Listing all files in storage directory: {}", root);
            List<FileInfo> files = Files.list(root)
                    .filter(Files::isRegularFile)
                    .map(path -> mapper.mapToFileInfo(path, request))
                    //.map(path -> baseUrl + DOWNLOAD_PATH + path.getFileName().toString())
                    //.map(path -> path.getFileName().toString())
                    .toList();
            log.info("Found {} file(s) in storage", files.size());
            return files;
        } catch (IOException e) {
            log.error("Failed to list files in directory: {}", root, e);
            throw new RuntimeException("Could not list files", e);
        }
    }

    @Override
    public FileInfo getFile(String id) {
        log.debug("Fetching file with ID: {}", id);
        Path filePath = root.resolve(id);
        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            log.info("File found: {}", filePath);
            return mapper.mapToFileInfo(filePath, request);
        } else {
            log.warn("File not found: {}", id);
            throw new RuntimeException("File not found: " + id);
        }
    }

    @Override
    public boolean isFileExist(String id) {
        Path filePath = root.resolve(id);
        boolean exists = Files.exists(filePath) && Files.isRegularFile(filePath);
        log.debug("Checking existence for file '{}': {}", id, exists);
        return exists;

        /*try (Stream<Path> paths = Files.walk(root)) {
            return paths
                    .filter(Files::isRegularFile)
                    .anyMatch(path -> path.getFileName().toString().equals(id));
        } catch (IOException e) {
            throw new RuntimeException("Failed to check if file exists: " + id, e);
        }*/
    }

    @Override
    public FileInfo upload(MultipartFile file, String bucketName) {
        try {
            log.info("Uploading file: {}, bucket: {}", file.getOriginalFilename(), bucketName);
            Path directory = (bucketName == null || bucketName.isBlank()) ? root : root.resolve(bucketName);

            // Create directory if it doesn't exist
            if (!Files.exists(directory)) {
                log.info("Bucket directory does not exist. Creating: {}", directory);
                Files.createDirectories(directory);
            }

            Path destination = directory.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            log.info("File uploaded successfully to: {}", destination);
            FileInfo fileInfo = mapper.mapToFileInfo(destination, request);
            log.info("Image uploaded, Download URL: {}", fileInfo.getDownloadUrl());

            return fileInfo;
        } catch (IOException e) {
            log.error("Could not upload file: {}, bucket: {}", file.getOriginalFilename(), bucketName, e);
            throw new RuntimeException("Could not store the file" + (bucketName != null ? " in bucket " + bucketName : ""), e);
        }
    }

    @Override
    public FileInfo upload(MultipartFile file) {
        return upload(file, null);
    }


    @Override
    public String upload(File file) {
        try {
            log.debug("Uploading file from filesystem: {}", file.getAbsolutePath());
            Path destination = root.resolve(file.getName());
            Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            log.info("File uploaded from filesystem to: {}", destination);
            return destination.toString();
        } catch (IOException e) {
            log.error("Could not upload file from filesystem: {}", file.getAbsolutePath(), e);
            throw new RuntimeException("Could not store the file", e);
        }
    }

    @Override
    public InputStream  download(String fileId) {
        try {
            log.debug("Downloading file: {}", fileId);
            //return Files.readAllBytes(root.resolve(fileId));
            return Files.newInputStream(root.resolve(fileId));
        } catch (IOException e) {
            log.error("Could not download file: {}", fileId, e);
            throw new RuntimeException("Could not read the file", e);
        }
    }

    @Override
    public void delete(String fileId) {
        try {
            Path path = root.resolve(fileId);
            boolean deleted = Files.deleteIfExists(path);
            if (deleted) {
                log.info("File deleted: {}", fileId);
            } else {
                log.warn("Attempted to delete non-existing file: {}", fileId);
            }
        } catch (IOException e) {
            log.error("Could not delete file: {}", fileId, e);
            throw new RuntimeException("Could not delete the file", e);
        }
    }


}
