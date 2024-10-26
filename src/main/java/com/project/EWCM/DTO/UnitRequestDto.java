package com.project.EWCM.DTO;

import com.project.EWCM.pojo.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitRequestDto {
    private String unitName;

    private String unitAddress;

    private String unitPhoneNumber;

    private String unitNumber;

    private List<Account> accountListOfUnit;

    private com.project.EWCM.pojo.Account unitHead;

}
