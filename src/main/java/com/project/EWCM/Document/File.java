package com.project.EWCM.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.project.EWCM.pojo.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "file")
public class File {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String filename;
    private String extension;
    private String mimeType;
    private long size;
    private String path;
    private Date createdDate;
    private Account createdBy;
    private Date updatedDate;
    private Date updatedBy;



}
