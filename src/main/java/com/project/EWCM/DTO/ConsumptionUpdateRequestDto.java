package com.project.EWCM.DTO;

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

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionUpdateRequestDto {
    private ElectricityConsumption electricityConsumption;
    private WaterConsumption waterConsumption;
}
