package com.project.EWCM.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.project.EWCM.DTO.AccountDto;
import com.project.EWCM.pojo.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "unit")
public class Unit {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String unitName;
    private String unitAddress;
    private String unitPhoneNumber;
    private String unitNumber;
    private List<Account> accountListOfUnit;
    private Account unitHead;
    private int unitLevel;
    private com.project.EWCM.pojo.Unit parentUnit;
    private Date createdDate;
    private Date updatedDate;
    private Account createdBy;
    private Account updatedBy;
}
