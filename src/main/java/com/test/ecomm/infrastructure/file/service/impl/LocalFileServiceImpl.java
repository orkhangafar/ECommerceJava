package com.test.ecomm.infrastructure.file.service.impl;

import com.test.ecomm.common.exception.BadRequestException;
import com.test.ecomm.infrastructure.file.service.FileService;
import com.test.ecomm.infrastructure.file.dto.FileResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(name = "file.storage.provider", havingValue = "local")
public class LocalFileServiceImpl implements FileService {

    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    @Override
    public FileResponse uploadImage(String path, MultipartFile file) {
        validateFile(file);
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new RuntimeException("Faylın adı oxuna bilmədi");
        }

        String fileName = UUID.randomUUID().toString()
                .concat(originalFileName.substring(originalFileName.lastIndexOf(".")));
        try {
            File folder = new File(path);
            if (!folder.exists() && !folder.mkdirs()) {
                throw new RuntimeException("Qovluq yaradıla bilmədi: " + path);
            }

            Files.copy(file.getInputStream(), Paths.get(path).resolve(fileName));

            return FileResponse.builder()
                    .fileUrl(fileName)
                    .fileId(path + File.separator + fileName)
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("Fayl lokal diskə yazılarkən xəta: " + e.getMessage());
        }
    }

    @Override
    public void deleteImage(String fileId) {
        if (fileId == null || fileId.isBlank()) return;
        try {
            Files.deleteIfExists(Paths.get(fileId));
            log.info("Lokal fayl silindi: {}", fileId);
        } catch (IOException e) {
            log.warn("Lokal fayl silinə bilmədi: {}", e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Fayl boşdur");
        }
        if (file.getContentType() == null || !ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Yalnız JPEG, PNG və WebP formatları qəbul edilir");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BadRequestException("Faylın ölçüsü 5MB-dan çox ola bilməz");
        }
    }
}