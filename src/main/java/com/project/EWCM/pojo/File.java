package com.project.EWCM.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "file")
public class File {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String filename;
    private String mimeType;
    private long size;
    private String path;

}
