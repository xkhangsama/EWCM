package com.project.EWCM.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElectricityConsumption {
    private int type;
    @NotNull(message = "Number cannot be null")
    private long number;
    private String file;

}
