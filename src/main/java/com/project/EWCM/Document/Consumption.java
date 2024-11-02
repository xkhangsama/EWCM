package com.project.EWCM.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.project.EWCM.pojo.Account;
import com.project.EWCM.pojo.ElectricityConsumption;
import com.project.EWCM.pojo.Unit;
import com.project.EWCM.pojo.WaterConsumption;
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
@Document(collection = "consumption")
public class Consumption {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private ElectricityConsumption electricityConsumption;
    private WaterConsumption waterConsumption;
    private Unit unit;
    private long electricityExceedAmount; // Lượng điện vượt - thì vượt + thì chưa vượt
    private long waterExceedAmount; // Lượng nước vượt - thì vượt + thì chưa vượt
    private Account createdBy;
    private Account updatedBy;
    private Date createdDate;
    private Date updatedDate;
}
