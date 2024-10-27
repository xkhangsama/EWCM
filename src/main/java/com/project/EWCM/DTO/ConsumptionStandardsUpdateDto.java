package com.project.EWCM.DTO;

import com.project.EWCM.pojo.ElectricityConsumption;
import com.project.EWCM.pojo.WaterConsumption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionStandardsUpdateDto {
    private int unitLevel;
    private ElectricityConsumption electricityConsumptionMax;
    private WaterConsumption waterConsumptionMax;
}
