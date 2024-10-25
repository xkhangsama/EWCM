package com.project.EWCM.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AffectedRowsDto implements Serializable {
    private int affectedRows = 0;
    private String error;

    public AffectedRowsDto(int affectedRows) {
        this.affectedRows = affectedRows;
    }
}
