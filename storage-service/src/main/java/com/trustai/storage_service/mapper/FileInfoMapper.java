package com.trustai.storage_service.mapper;

import com.trustai.storage_service.dto.FileInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;
import java.util.UUID;

@Component
@Slf4j
public class FileInfoMapper {
    public static final String IMAGE_PATH = "/images"; // CommonConstants
    public final String THUMBNAIL_PATH = IMAGE_PATH + "/thumbnail";


    @Value("${app.base-url:http://trustai.co.in/}")
    private String baseUrl;

    private String getBaseUrl(HttpServletRequest request) {
        //return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        String scheme = request.getScheme();
        String hostHeader = request.getHeader("Host");
        String contextPath = request.getContextPath();

        log.info("Request scheme: {}", scheme);
        log.info("Request Host header: {}", hostHeader);
        log.info("Request context path: {}", contextPath);

        String baseUrl1 = scheme  + "://" + hostHeader  + contextPath;
        log.info("Manually constructed baseUrl1: {}", baseUrl1);

        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
        log.info("Base URL from ServletUriComponentsBuilder: {}", baseUrl);

        return baseUrl;
    }

    private String getBaseUrl(HttpServletRequest request, String fileName) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        return baseUrl + IMAGE_PATH + "/" + fileName;
    }

    public FileInfo mapToFileInfo(Path path, HttpServletRequest request) {
        FileInfo info = new FileInfo();
        try {
            // Basic file data
            //info.setId(UUID.randomUUID().toString());
            info.setId(path.getFileName().toString());
            info.setOriginalFileName(path.getFileName().toString());
            info.setPath(path.toString());
            info.setExtension(getFileExtension(path));
            info.setSizeInKb(Files.size(path) / 1024);

            // Timestamps
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            info.setCreatedAt(attrs.creationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            info.setLastModifiedAt(attrs.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

            // MIME type
            String mimeType = Files.probeContentType(path);
            info.setMimeType(mimeType);
            info.setImage(mimeType != null && mimeType.startsWith("image"));

            // Image dimensions
            if (info.isImage()) {
                BufferedImage img = ImageIO.read(path.toFile());
                if (img != null) {
                    info.setWidth(img.getWidth());
                    info.setHeight(img.getHeight());
                }
            }

            // Download/thumbnail URLs (replace with your app's logic)
            String filename = path.getFileName().toString();
            info.setDownloadUrl(baseUrl + IMAGE_PATH + "/" + filename);
            info.setThumbnailUrl(baseUrl + THUMBNAIL_PATH + "/" + filename);

        } catch (IOException e) {
            throw new RuntimeException("Error mapping file info for: " + path, e);
        }

        return info;
    }


    private String getFileExtension(Path path) {
        String name = path.getFileName().toString();
        int dotIndex = name.lastIndexOf('.');
        return (dotIndex >= 0) ? name.substring(dotIndex + 1).toLowerCase() : "";
    }
}
