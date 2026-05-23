package com.test.ecomm.infrastructure.file.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class FileResponse {
    private String fileUrl;
    private String fileId;
}
