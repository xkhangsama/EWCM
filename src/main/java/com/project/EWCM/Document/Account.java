package com.project.EWCM.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.project.EWCM.DTO.UnitDto;
import com.project.EWCM.pojo.Unit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "account")
public class Account {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String type;
    private Unit unit;
    private String temp;
    private boolean isHead;
    private Date createdDate;
    private Date updatedDate;
    private com.project.EWCM.pojo.Account createdBy;
    private com.project.EWCM.pojo.Account updatedBy;
}
