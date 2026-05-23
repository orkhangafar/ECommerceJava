package com.test.ecomm.infrastructure.file.service;

import com.test.ecomm.infrastructure.file.dto.FileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    FileResponse uploadImage(String path, MultipartFile file);
    void deleteImage(String fileId);
}
