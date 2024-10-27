package com.project.EWCM.Service;

import com.project.EWCM.DTO.*;
import com.project.EWCM.Document.Account;
import com.project.EWCM.Document.ConsumptionStandards;
import com.project.EWCM.exception.HttpException;
import com.project.EWCM.repository.ConsumptionStandardsRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
public class ConsumptionStandardsService {
    @Autowired
    private ConsumptionStandardsRepository consumptionStandardsRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UnitService unitService;


    Logger logger = LoggerFactory.getLogger(ConsumptionService.class);


    public IdDto createConsumptionStandards(ConsumptionStandardsDto consumptionStandardsDto, String username) {

        ConsumptionStandards newStandards = new ConsumptionStandards();
        Date currentDate = new Date();

        Account account = accountService.findAccountByUsername(username);

        if(Objects.nonNull(consumptionStandardsDto.getElectricityConsumptionMax())){
            newStandards.setElectricityConsumptionMax(consumptionStandardsDto.getElectricityConsumptionMax());
        }

        if(Objects.nonNull(consumptionStandardsDto.getWaterConsumptionMax())){
            newStandards.setWaterConsumptionMax(consumptionStandardsDto.getWaterConsumptionMax());
        }

        newStandards.setCreatedDate(currentDate);
        newStandards.setUpdatedDate(currentDate);

        com.project.EWCM.pojo.Account createdBy =
                new com.project.EWCM.pojo.Account(account.getId(),account.getUsername(),account.getFullName(), account.getEmail(), account.getType());
        newStandards.setCreatedBy(createdBy);

        consumptionStandardsRepository.save(newStandards);
        logger.info("EWCM-Saved Consumption Standards Data: " + newStandards.toString());
        return new IdDto(newStandards.getId());
    }

    public ConsumptionStandardResponseDto getConsumptionStandardsDetail(ObjectId id) {
        ConsumptionStandardResponseDto result = new ConsumptionStandardResponseDto();

        ConsumptionStandards consumptionStandards = findConsumptionStandardsById(id);

        if(Objects.nonNull(consumptionStandards.getUnitLevel())){
            result.setUnitLevel(consumptionStandards.getUnitLevel());
        }
        if(Objects.nonNull(consumptionStandards.getElectricityConsumptionMax())){
            result.setElectricityConsumptionMax(consumptionStandards.getElectricityConsumptionMax());
        }
        if(Objects.nonNull(consumptionStandards.getWaterConsumptionMax())){
            result.setWaterConsumptionMax(consumptionStandards.getWaterConsumptionMax());
        }
        logger.info("EWCM-Get Consumption Standards Data: " + result.toString());
        return result;
    }

    public AffectedRowsDto updateConsumptionStandards(ConsumptionStandardsUpdateDto updateDto, String username, ObjectId id) {
        AffectedRowsDto affectedRowsDto = new AffectedRowsDto();

        ConsumptionStandards oldConsumptionStandards = findConsumptionStandardsById(id);

        Account account = accountService.findAccountByUsername(username);

        if(Objects.nonNull(updateDto.getUnitLevel())){
            oldConsumptionStandards.setUnitLevel(updateDto.getUnitLevel());
        }
        if(Objects.nonNull(updateDto.getElectricityConsumptionMax())){
            oldConsumptionStandards.setElectricityConsumptionMax(updateDto.getElectricityConsumptionMax());
        }
        if(Objects.nonNull(updateDto.getWaterConsumptionMax())){
            oldConsumptionStandards.setWaterConsumptionMax(updateDto.getWaterConsumptionMax());
        }
        oldConsumptionStandards.setUpdatedDate(new Date());

        com.project.EWCM.pojo.Account updatedBy =
                new com.project.EWCM.pojo.Account(account.getId(),account.getUsername(),account.getFullName(), account.getEmail(), account.getType());
        oldConsumptionStandards.setUpdatedBy(updatedBy);
        consumptionStandardsRepository.save(oldConsumptionStandards);

        logger.info("EWCM-Update Consumption Standards Data: " + oldConsumptionStandards.toString());
        affectedRowsDto.setAffectedRows(1);
        return affectedRowsDto;
    }

    private ConsumptionStandards findConsumptionStandardsById(ObjectId id){
        ConsumptionStandards consumptionStandards = consumptionStandardsRepository.findById(id).orElseThrow(() ->
                new HttpException(10004, "Consumption Standard not found.", HttpServletResponse.SC_NOT_FOUND)
        );
        return consumptionStandards;
    }

    public AffectedRowsDto deleteConsumptionStandards(String username, ObjectId id) {
        AffectedRowsDto affectedRowsDto = new AffectedRowsDto();

        ConsumptionStandards oldConsumptionStandards = findConsumptionStandardsById(id);

        consumptionStandardsRepository.deleteById(id);

        logger.info("EWCM-Delete Consumption Standards Data: " + oldConsumptionStandards.toString());
        affectedRowsDto.setAffectedRows(1);
        return affectedRowsDto;
    }
}
