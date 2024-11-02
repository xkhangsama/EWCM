package com.project.EWCM.DTO;

import com.project.EWCM.pojo.Unit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionTotalDto {
    private Unit unit;
    private long totalElectricityConsumption;
    private long totalWaterConsumption;

    public ConsumptionTotalDto(long totalElectricityConsumption, long totalWaterConsumption) {
        this.totalWaterConsumption = totalWaterConsumption;
        this.totalElectricityConsumption = totalElectricityConsumption;
    }
}
