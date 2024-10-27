package com.project.EWCM.DTO;

import com.project.EWCM.pojo.ElectricityConsumption;
import com.project.EWCM.pojo.WaterConsumption;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionStandardsDto {
    @NotNull(message = "Unit level cannot be null")
    private int unitLevel;
    @NotNull(message = "Electricity Consumption cannot be null")
    private ElectricityConsumption electricityConsumptionMax;
    @NotNull(message = "Water Consumption cannot be null")
    private WaterConsumption waterConsumptionMax;
}
