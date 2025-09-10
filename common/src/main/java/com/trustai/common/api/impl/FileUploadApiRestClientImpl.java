package com.trustai.common.api.impl;

import com.trustai.common.api.FileUploadApi;
import com.trustai.common.utils.RestCallHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class FileUploadApiRestClientImpl implements FileUploadApi {
    private final RestClient restClient;

    public FileUploadApiRestClientImpl(@Qualifier("v1ApiRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ResponseEntity<Map<String, Object>> uploadMultipleFiles(MultipartFile[] files) {
        log.info("Calling uploadMultipleFiles with {} file(s)", files.length);

        // Prepare multipart body
        MultiValueMap<String, Object> multipartBody = new LinkedMultiValueMap<>();

        for (MultipartFile file : files) {
            try {
                // Wrap MultipartFile content in ByteArrayResource
                ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename();
                    }
                };

                multipartBody.add("files", resource);

            } catch (IOException e) {
                throw new RuntimeException("Failed to read file bytes", e);
            }
        }

        // Perform REST call with error handling using your RestCallHandler
        return RestCallHandler.handleRestCall(() ->
                        restClient.post()
                                .uri("/images/upload-multiple")
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .body(multipartBody)
                                .retrieve()
                                .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {})
                , "uploadMultipleFiles");
    }

    @Override
    public String uploadFile(MultipartFile file) {
//        return uploadMultipleFiles(new MultipartFile[]{file});
        String imageUrl = null;
        try {
            Map<String, Object> uploadFileMap = uploadMultipleFiles(new MultipartFile[]{file}).getBody();
            Map.Entry<String, Object> entry = uploadFileMap.entrySet().stream().findFirst().orElse(null);
            if (entry != null) {
                Object innerValue = entry.getValue();

                if (innerValue instanceof Map) {
                    Map<String, Object> innerMap = (Map<String, Object>) innerValue;

                    String downloadUrl = (String) innerMap.get("downloadUrl");
                    //System.out.println("Download URL: " + downloadUrl);
                    imageUrl = downloadUrl;
                }
            }
            //imageUrl = (String) upload.get("downloadUrl");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("file upload failed");
        }
        return imageUrl;
    }
}
