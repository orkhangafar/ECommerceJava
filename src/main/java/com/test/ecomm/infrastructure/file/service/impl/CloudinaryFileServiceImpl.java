package com.test.ecomm.infrastructure.file.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.test.ecomm.common.exception.BadRequestException;
import com.test.ecomm.infrastructure.file.service.FileService;
import com.test.ecomm.infrastructure.file.dto.FileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@ConditionalOnProperty(name = "file.storage.provider", havingValue = "cloudinary")
@RequiredArgsConstructor
public class CloudinaryFileServiceImpl implements FileService {

    private final Cloudinary cloudinary;

    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    @Override
    public FileResponse uploadImage(String path, MultipartFile file) {
        validateFile(file);
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", path,
                            "resource_type", "image"
                    ));

            return FileResponse.builder()
                    .fileUrl((String) result.get("secure_url"))
                    .fileId((String) result.get("public_id"))
                    .build();

        } catch (IOException e) {
            throw new BadRequestException("Cloudinary-ə yüklənərkən xəta baş verdi: " + e.getMessage());
        }
    }

    @Override
    public void deleteImage(String fileId) {
        if (fileId == null || fileId.isBlank()) return;
        try {
            cloudinary.uploader().destroy(fileId, ObjectUtils.emptyMap());
            log.info("Fayl Cloudinary-dən silindi: {}", fileId);
        } catch (IOException e) {
            log.warn("Cloudinary-dən fayl silinə bilmədi: {}", e.getMessage());
        }
    }

//    Köməkçi metodlar----------

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