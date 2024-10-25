package com.project.EWCM.Service;

import com.project.EWCM.DTO.IdDto;
import com.project.EWCM.DTO.UnitDto;
import com.project.EWCM.DTO.UnitRequestDto;
import com.project.EWCM.Document.Account;
import com.project.EWCM.Document.Unit;
import com.project.EWCM.exception.HttpException;
import com.project.EWCM.repository.AccountRepository;
import com.project.EWCM.repository.UnitRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

@Service
public class UnitService {
    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    AccountRepository accountRepository;

    @Value("${project.app.maxLevel}")
    private int maxLevel;

    Logger logger = LoggerFactory.getLogger(UnitService.class);

    public IdDto createUnit(UnitRequestDto unitDto, String username) {
        Optional<Account> optional = accountRepository.findByUsername(username);
        int levelOfUnitAccount = 0;
        if(!optional.get().getType().equals("admin")){
            levelOfUnitAccount = optional.get().getUnit().getUnitLevel();
            if(levelOfUnitAccount >= maxLevel){
                throw new HttpException(10007,"Users are not allowed to create units of a lower level.", HttpServletResponse.SC_BAD_REQUEST);
            }
        }

        Unit newUnit = new Unit();
        if(Objects.nonNull(unitDto.getUnitName())){
            newUnit.setUnitName(unitDto.getUnitName());
        }
        if(Objects.nonNull(unitDto.getUnitAddress())){
            newUnit.setUnitAddress(unitDto.getUnitAddress());
        }
        if(Objects.nonNull(unitDto.getUnitNumber())){
            newUnit.setUnitNumber(unitDto.getUnitNumber());
        }
        if(Objects.nonNull(unitDto.getUnitPhoneNumber())){
            newUnit.setUnitPhoneNumber(unitDto.getUnitPhoneNumber());
        }
        if(Objects.nonNull(unitDto.getUnitHead())){
            newUnit.setUnitHead(unitDto.getUnitHead());
        }
        if(Objects.nonNull(unitDto.getAccountListOfUnit())){
            newUnit.setAccountListOfUnit(unitDto.getAccountListOfUnit());
        }
        // set unit level = current unit level +1
        newUnit.setUnitLevel(levelOfUnitAccount + 1);
        unitRepository.save(newUnit);
        logger.info("EWCD-Saved Unit Data: " + newUnit.toString());
        return new IdDto(newUnit.getId());
    }
}
