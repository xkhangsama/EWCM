package com.project.EWCM.Service;

import com.project.EWCM.DTO.*;
import com.project.EWCM.Document.Account;
import com.project.EWCM.Document.Consumption;
import com.project.EWCM.Document.Unit;
import com.project.EWCM.exception.HttpException;
import com.project.EWCM.repository.ConsumptionRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConsumptionService {
    @Autowired
    private ConsumptionRepository consumptionRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UnitService unitService;


    Logger logger = LoggerFactory.getLogger(ConsumptionService.class);

    public IdDto createConsumption(ConsumptionRequestDto consumptionRequestDto, String username) {

        Date currentDate = new Date();
        Consumption newConsumption = new Consumption();

        Account account = accountService.findAccountByUsername(username);

        Unit unit = unitService.findUnitById(consumptionRequestDto.getUnit().getId());

        if(!unit.getUnitHead().getId().equals(account.getId()) && !unit.getCreatedBy().getId().equals(account.getId())){
            throw new HttpException(10003, "Users are not allowed.", HttpServletResponse.SC_FORBIDDEN);
        }


        if(Objects.nonNull(consumptionRequestDto.getElectricityConsumption())){
            newConsumption.setElectricityConsumption(consumptionRequestDto.getElectricityConsumption());
        }

        if(Objects.nonNull(consumptionRequestDto.getWaterConsumption())){
            newConsumption.setWaterConsumption(consumptionRequestDto.getWaterConsumption());
        }

        if(Objects.nonNull(consumptionRequestDto.getUnit())){
            newConsumption.setUnit(consumptionRequestDto.getUnit());
        }

        newConsumption.setCreatedDate(currentDate);
        newConsumption.setUpdatedDate(currentDate);

        com.project.EWCM.pojo.Account createdBy =
                new com.project.EWCM.pojo.Account(account.getId(),account.getUsername(),account.getFullName(), account.getEmail(), account.getType());
        newConsumption.setCreatedBy(createdBy);

        consumptionRepository.save(newConsumption);
        logger.info("EWCM-Saved Consumption Data: " + newConsumption.toString());
        return new IdDto(newConsumption.getId());
    }

    public ConsumptionDetailResponseDto getConsumptionDetail(String username, ObjectId id) {
        ConsumptionDetailResponseDto result = new ConsumptionDetailResponseDto();
        Account account = accountService.findAccountByUsername(username);
        Consumption consumption = findConsumptionById(id);
        Unit unit = unitService.findUnitById(consumption.getUnit().getId());

        // Lấy danh sách đơn vị cấp dưới dựa trên ID của đơn vị hiện tại của người muốn xem thông tin báo cáo
        List<Unit> accessibleUnits = getAccessibleUnits(account.getUnit().getId());


        //Chỉ xem được chi tiết báo cáo khi tạo ra nó hoặc trưởng đơn vị của báo cáo đó
        if(!consumption.getCreatedBy().getId().equals(account.getId())
                && !unit.getUnitHead().getId().equals(account.getId())){
            throw new HttpException(10003, "Users are not allowed.", HttpServletResponse.SC_FORBIDDEN);
        }
        result = mappingConsumption(consumption);
        logger.info("EWCM-Get Consumption Detail: " + result.toString());
        return result;
    }

    private List<Unit> getAccessibleUnits(ObjectId accountUnitId) {
        List<Unit> accessibleUnits = new ArrayList<>();
        Queue<Unit> queue = new LinkedList<>();

        Unit currentUnit = unitService.findUnitById(accountUnitId);
        // Sử dụng BFS để duyệt qua tất cả các đơn vị cấp dưới
        while (!queue.isEmpty()) {
            Unit unit = queue.poll();
            accessibleUnits.add(unit);
//            List<Unit> subUnits = unitRepository.findByParentUnitId(unit.getId());  // Bổ sung thêm parentUnitId cho Unit collection
//            queue.addAll(subUnits);
        }
        queue.add(currentUnit);
        return accessibleUnits;
    }

    private Consumption findConsumptionById(ObjectId id){
        return consumptionRepository.findById(id).orElseThrow(() ->
                new HttpException(10004, "Consumption not found.", HttpServletResponse.SC_NOT_FOUND)
        );
    }

    private ConsumptionDetailResponseDto mappingConsumption(Consumption consumption){
        ConsumptionDetailResponseDto result = new ConsumptionDetailResponseDto();
        if(Objects.nonNull(consumption.getId())){
            result.setId(consumption.getId());
        }
        if(Objects.nonNull(consumption.getElectricityConsumption())){
            result.setElectricityConsumption(consumption.getElectricityConsumption());
        }
        if(Objects.nonNull(consumption.getWaterConsumption())){
            result.setWaterConsumption(consumption.getWaterConsumption());
        }
        if(Objects.nonNull(consumption.getUnit())){
            result.setUnit(consumption.getUnit());
        }
        if(Objects.nonNull(consumption.getCreatedBy())){
            result.setCreatedBy(consumption.getCreatedBy());
        }
        if(Objects.nonNull(consumption.getCreatedBy())){
            result.setCreatedBy(consumption.getCreatedBy());
        }
        if(Objects.nonNull(consumption.getCreatedDate())){
            result.setCreatedDate(consumption.getCreatedDate());
        }
        return result;
    }

    public AffectedRowsDto updateConsumption(ConsumptionUpdateRequestDto consumptionUpdateRequestDto, String username, ObjectId id) {
        AffectedRowsDto affectedRowsDto = new AffectedRowsDto();

        Account account = accountService.findAccountByUsername(username);

        Consumption oldConsumption = findConsumptionById(id);

        Unit unit = unitService.findUnitById(oldConsumption.getUnit().getId());

        if(!oldConsumption.getCreatedBy().getId().equals(account.getId())
                && !unit.getUnitHead().getId().equals(account.getId())
                && !unit.getCreatedBy().getId().equals(account.getId())){
            throw new HttpException(10003, "Users are not allowed.", HttpServletResponse.SC_FORBIDDEN);
        }

        if(Objects.nonNull(consumptionUpdateRequestDto.getElectricityConsumption())){
            oldConsumption.setElectricityConsumption(consumptionUpdateRequestDto.getElectricityConsumption());
        }

        if(Objects.nonNull(consumptionUpdateRequestDto.getWaterConsumption())){
            oldConsumption.setWaterConsumption(consumptionUpdateRequestDto.getWaterConsumption());
        }
        com.project.EWCM.pojo.Account updatedBy =
                new com.project.EWCM.pojo.Account(account.getId(),account.getUsername(),account.getFullName(), account.getEmail(), account.getType());
        oldConsumption.setUpdatedBy(updatedBy);
        oldConsumption.setUpdatedDate(new Date());
        consumptionRepository.save(oldConsumption);
        affectedRowsDto.setAffectedRows(1);
        logger.info("EWCM-Get Consumption Detail: " + oldConsumption.toString());
        return affectedRowsDto;
    }

    public AffectedRowsDto deleteConsumption(String username, ObjectId id) {
        AffectedRowsDto affectedRowsDto = new AffectedRowsDto();

        Account account = accountService.findAccountByUsername(username);

        Consumption consumption = findConsumptionById(id);

        Unit unit = unitService.findUnitById(consumption.getUnit().getId());

        if(!consumption.getCreatedBy().getId().equals(account.getId())
                && !unit.getUnitHead().getId().equals(account.getId())
                && !unit.getCreatedBy().getId().equals(account.getId())){
            throw new HttpException(10003, "Users are not allowed.", HttpServletResponse.SC_FORBIDDEN);
        }

        consumptionRepository.deleteById(id);

        affectedRowsDto.setAffectedRows(1);
        return affectedRowsDto;
    }
}
