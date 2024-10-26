package com.project.EWCM.DTO;

import com.project.EWCM.pojo.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitDto {
    private String unitName;

    private String unitAddress;

    private String unitPhoneNumber;

    private String unitNumber;

    private List<com.project.EWCM.pojo.Account> accountListOfUnit;

    private Account unitHead;

    private int UnitLevel;
}
