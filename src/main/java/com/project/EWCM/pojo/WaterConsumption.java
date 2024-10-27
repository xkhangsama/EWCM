package com.project.EWCM.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterConsumption {
    private int type;
    private long number;
    private String file;
}
