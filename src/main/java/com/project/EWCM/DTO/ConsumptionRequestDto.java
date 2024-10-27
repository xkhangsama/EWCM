package com.project.EWCM.DTO;

import com.project.EWCM.pojo.ElectricityConsumption;
import com.project.EWCM.pojo.Unit;
import com.project.EWCM.pojo.WaterConsumption;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionRequestDto {

    private ElectricityConsumption electricityConsumption;
    private WaterConsumption waterConsumption;
    @NotNull(message = "Unit cannot be null")
    private Unit unit;
}
