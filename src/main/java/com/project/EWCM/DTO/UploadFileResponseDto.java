package com.project.EWCM.DTO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileResponseDto {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    private String fileName;

    private String mimeType;
    private long size;
    private String filePath;
}
