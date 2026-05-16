package com.test.ecomm.infrastructure.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadImage(String path, MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new RuntimeException("Faylın adı oxuna bilmədi");
        }
        String fileName = UUID.randomUUID().toString()
                .concat(originalFileName.substring(originalFileName.lastIndexOf(".")));
        try {
            File folder = new File(path);
            if (!folder.exists()) folder.mkdirs();
            Files.copy(file.getInputStream(), Paths.get(path + File.separator + fileName));
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Fayl yüklənərkən xəta baş verdi: " + e.getMessage());
        }
    }

    @Override
    public void deleteImage(String path, String fileName) {
        if (fileName == null || fileName.equals("default.png")) return;
        try {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
        } catch (IOException e) {
            log.warn("Köhnə fayl silinə bilmədi: {}", e.getMessage());
        }
    }
}