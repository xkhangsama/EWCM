package com.project.EWCM.DTO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.project.EWCM.pojo.ElectricityConsumption;
import com.project.EWCM.pojo.WaterConsumption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionStandardResponseDto {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private int unitLevel;
    private ElectricityConsumption electricityConsumptionMax;
    private WaterConsumption waterConsumptionMax;
}
