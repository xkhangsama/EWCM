package com.project.EWCM.DTO;

import com.project.EWCM.pojo.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDownloadResponseDto {
    private Resource resource;
    private String fileMimeType;
    private String filename;
    private Account createdBy;
    private long size;
}
