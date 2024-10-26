package com.project.EWCM.Service;

import com.project.EWCM.DTO.AccountDto;
import com.project.EWCM.DTO.IdDto;
import com.project.EWCM.Document.Account;
import com.project.EWCM.repository.AccountRepository;
import com.project.EWCM.exception.HttpException;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public AccountDto getAccountDetail(String username) {
        AccountDto accountDetail = new AccountDto();
        Account account = accountRepository.findByUsername(username).orElseThrow(() ->
                new HttpException(10004, "User not found.", HttpServletResponse.SC_NOT_FOUND)
        );
        accountDetail.setUsername(account.getUsername());
        accountDetail.setEmail(account.getEmail());
        accountDetail.setFullName(account.getFullName());
        accountDetail.setTemp(account.getTemp());
        if(Objects.nonNull(account.getUnit())) {
            accountDetail.setUnit(account.getUnit());
        }
        logger.info("EWCD-Get Account Data: " + accountDetail.toString());
        return accountDetail;
    }

    public List<com.project.EWCM.pojo.Account> getAccountList(String username) {
        List<com.project.EWCM.pojo.Account> result = new ArrayList<>();
        // Kiểm tra tài khoản có được lấy dữ liệu hay không
        Account account = accountRepository.findByUsername(username).orElseThrow(() ->
                new HttpException(10004, "User not found.", HttpServletResponse.SC_NOT_FOUND)
        );
        if(!account.isHead()){
            return result;
        }

        List<Account> listaccount = accountRepository.findByUnitIsNull();
        // Mapping dữ liệu từ Account sang AccountDto
        result = listaccount.stream()
                .filter(acc -> !"admin".equalsIgnoreCase(acc.getType()))
                .map(acc -> new com.project.EWCM.pojo.Account(acc.getId(), acc.getUsername(), acc.getFullName(), acc.getEmail(), acc.getType())) // Thay đổi theo các thuộc tính của AccountDto
                .collect(Collectors.toList());

        logger.info("EWCD-Get Account List: " + result.toString());
        return result;
    }
}
