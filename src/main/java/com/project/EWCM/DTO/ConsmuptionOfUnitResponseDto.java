package com.project.EWCM.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsmuptionOfUnitResponseDto {
    private List<ConsumptionDetailResponseDto> subUnitConsumption;

    private List<ConsumptionTotalDto> totalList;
}
