package com.test.ecomm.infrastructure.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadImage(String path, MultipartFile file);
    void deleteImage(String path, String fileName);
}
