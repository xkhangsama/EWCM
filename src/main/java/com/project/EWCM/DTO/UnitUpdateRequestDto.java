package com.project.EWCM.DTO;

import com.project.EWCM.Document.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitUpdateRequestDto {
    private String unitName;

    private String unitAddress;

    private String unitPhoneNumber;

    private String unitNumber;

    private List<Account> accountListOfUnit;

    private AccountDto unitHead;

}
