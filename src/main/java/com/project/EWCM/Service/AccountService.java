package com.project.EWCM.Service;

import com.project.EWCM.DTO.AccountDto;
import com.project.EWCM.DTO.IdDto;
import com.project.EWCM.Document.Account;
import com.project.EWCM.repository.AccountRepository;
import com.project.EWCM.exception.HttpException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    Logger logger = LoggerFactory.getLogger(AccountService.class);

    //register
    public IdDto createAccount(AccountDto accountDto) {
        Account newAccount = new Account();
        if (accountRepository.existsByUsername(accountDto.getUsername())) {
            throw new HttpException(10007,"User already exists with username: " + accountDto.getUsername(), HttpServletResponse.SC_BAD_REQUEST);
        }

        if (accountRepository.existsByEmail(accountDto.getEmail())) {
            throw new HttpException(10007,"User already exists with email: " + accountDto.getEmail(), HttpServletResponse.SC_BAD_REQUEST);
        }
        if(Objects.nonNull(accountDto.getUsername())){
            newAccount.setUsername(accountDto.getUsername());
        }
        //encode pass
        if(Objects.nonNull(accountDto.getPassword())){
            String encodedPassword = passwordEncoder.encode(accountDto.getPassword());
            newAccount.setPassword(encodedPassword);
        }
        if(Objects.nonNull(accountDto.getFullName())){
            newAccount.setFullName(accountDto.getFullName());
        }

        if(Objects.nonNull(accountDto.getType())){
            newAccount.setType(accountDto.getType());
        }

        if(Objects.nonNull(accountDto.getEmail())){
            newAccount.setEmail(accountDto.getEmail());
        }

        if(Objects.nonNull(accountDto.getUnit())){
            newAccount.setUnit(accountDto.getUnit());
        }

        if(Objects.nonNull(accountDto.getTemp())){
            newAccount.setTemp(accountDto.getTemp());
        }
        newAccount.setCreatedDate(new Date());
        newAccount.setUpdatedDate(new Date());

        accountRepository.save(newAccount);
        logger.info("EWCD-Saved Account Data: " + newAccount.toString());
        return new IdDto(newAccount.getId());
    }
}
