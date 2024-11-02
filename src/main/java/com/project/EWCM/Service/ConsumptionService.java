package com.project.EWCM.Service;

import com.project.EWCM.DTO.*;
import com.project.EWCM.Document.Account;
import com.project.EWCM.Document.Consumption;
import com.project.EWCM.Document.ConsumptionStandards;
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
import java.util.stream.Collectors;

@Service
public class ConsumptionService {
    @Autowired
    private ConsumptionRepository consumptionRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UnitService unitService;
    @Autowired
    private ConsumptionStandardsService consumptionStandardsService;
    Logger logger = LoggerFactory.getLogger(ConsumptionService.class);

    public IdDto createConsumption(ConsumptionRequestDto consumptionRequestDto, String username) {

        Date currentDate = new Date();
        Consumption newConsumption = new Consumption();

        Account account = accountService.findAccountByUsername(username);

        Unit unit = unitService.findUnitById(account.getUnit().getId());
        int unitLevel = unit.getUnitLevel();

        if(!unit.getUnitHead().getId().equals(account.getId())){
            throw new HttpException(10003, "Users are not allowed.", HttpServletResponse.SC_FORBIDDEN);
        }

        ConsumptionStandards standards = consumptionStandardsService.findConsumpotionStandardsByLevel(unitLevel);

        if(Objects.nonNull(consumptionRequestDto.getElectricityConsumption())){
            newConsumption.setElectricityConsumption(consumptionRequestDto.getElectricityConsumption());
            newConsumption.setElectricityExceedAmount(standards.getElectricityConsumptionMax().getNumber()-consumptionRequestDto.getElectricityConsumption().getNumber());
        }

        if(Objects.nonNull(consumptionRequestDto.getWaterConsumption())){
            newConsumption.setWaterConsumption(consumptionRequestDto.getWaterConsumption());
            newConsumption.setWaterExceedAmount(standards.getWaterConsumptionMax().getNumber()-consumptionRequestDto.getWaterConsumption().getNumber());
        }

        if(Objects.nonNull(unit)){
            com.project.EWCM.pojo.Unit unitConsumption = new com.project.EWCM.pojo.Unit(unit.getId(), unit.getUnitName(), unit.getUnitLevel());
            newConsumption.setUnit(unitConsumption);
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

        if(!account.isHead()){
            throw new HttpException(10003, "Users are not allowed.", HttpServletResponse.SC_FORBIDDEN);
        }

        // Lấy danh sách đơn vị cấp dưới dựa trên ID của đơn vị hiện tại của người muốn xem thông tin báo cáo
        List<ObjectId> accessibleUnits = unitService.getAccessibleUnits(account.getUnit().getId());

        //Trưởng đơn vị là đơn vị cha của đơn vị báo cáo thì có thể xem chi tiết báo cáo
        if(!accessibleUnits.contains(consumption.getUnit().getId())){
            throw new HttpException(10003, "Users are not allowed.", HttpServletResponse.SC_FORBIDDEN);
        }

        result = mappingConsumption(consumption);
        logger.info("EWCM-Get Consumption Detail: " + result.toString());
        return result;
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
        if(Objects.nonNull(consumption.getElectricityExceedAmount())){
            result.setElectricityExceedAmount(consumption.getElectricityExceedAmount());
        }
        if(Objects.nonNull(consumption.getWaterExceedAmount())){
            result.setWaterExceedAmount(consumption.getWaterExceedAmount());
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
        int unitLevel = unit.getUnitLevel();

        if(!oldConsumption.getCreatedBy().getId().equals(account.getId())
                && !unit.getUnitHead().getId().equals(account.getId())
                && !unit.getCreatedBy().getId().equals(account.getId())){
            throw new HttpException(10003, "Users are not allowed.", HttpServletResponse.SC_FORBIDDEN);
        }

        ConsumptionStandards standards = consumptionStandardsService.findConsumpotionStandardsByLevel(unitLevel);

        if(Objects.nonNull(consumptionUpdateRequestDto.getElectricityConsumption())){
            oldConsumption.setElectricityConsumption(consumptionUpdateRequestDto.getElectricityConsumption());
            oldConsumption.setElectricityExceedAmount(standards.getElectricityConsumptionMax().getNumber()-consumptionUpdateRequestDto.getElectricityConsumption().getNumber());
        }

        if(Objects.nonNull(consumptionUpdateRequestDto.getWaterConsumption())){
            oldConsumption.setWaterConsumption(consumptionUpdateRequestDto.getWaterConsumption());
            oldConsumption.setWaterExceedAmount(standards.getWaterConsumptionMax().getNumber()-consumptionUpdateRequestDto.getWaterConsumption().getNumber());
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
        logger.info("EWCM-Delete Consumption : " + consumption.toString());
        affectedRowsDto.setAffectedRows(1);
        return affectedRowsDto;
    }

    public List<ConsumptionDetailResponseDto> getConsumptionOfUnit(String username, ObjectId unitId) {
        List<ConsumptionDetailResponseDto> result = new ArrayList<>();
        List<Consumption> consumptionList = new ArrayList<>();
        Account account = accountService.findAccountByUsername(username);

        boolean checkGetAll = false;

        if(!account.isHead()){
            throw new HttpException(10003, "Users are not allowed.", HttpServletResponse.SC_FORBIDDEN);
        }

        // Lấy danh sách đơn vị cấp dưới dựa trên ID của đơn vị hiện tại của người muốn xem thông tin báo cáo
        List<ObjectId> accessibleUnits = unitService.getAccessibleUnits(account.getUnit().getId());
//        .stream()
//                .filter(id -> unitService.getUnitLevel(id) <= account.getUnit().getUnitLevel()) // Lọc đơn vị cấp dưới
//                .collect(Collectors.toList());

        //Lấy thông tin comsumption toàn bộ đơn vị con của tài khoản nêú request không có unitId
        if(Objects.isNull(unitId)){
            unitId = account.getUnit().getId();
            checkGetAll = true;
        }

        //Đơn vị muốn lấy báo cáo nằm trong cây đơn vị mà user này quản lý
        if(!accessibleUnits.contains(unitId)){
            throw new HttpException(10003, "Users are not allowed.", HttpServletResponse.SC_FORBIDDEN);
        }

        List<ObjectId> subUnit = unitService.getAccessibleUnits(unitId);
        if(checkGetAll){
            consumptionList = consumptionRepository.findByUnitIdIn(accessibleUnits);
        }else {
            consumptionList = consumptionRepository.findByUnitIdIn(subUnit);
        }

        result = consumptionList.stream()
                .map(this::mappingConsumption)
                .collect(Collectors.toList());
        logger.info("EWCM-Get Consumption of Unit: " + result.toString());

        return result;
    }

    public List<ConsumptionTotalDto> getConsumptionTotal(String username) {
        List<ConsumptionTotalDto> consumptionTotalDtoList = new ArrayList<>();
        List<Consumption> consumptionList = new ArrayList<>();
        Account account = accountService.findAccountByUsername(username);

        if(!account.isHead()){
            throw new HttpException(10003, "Users are not allowed.", HttpServletResponse.SC_FORBIDDEN);
        }

        int currentLevel = account.getUnit().getUnitLevel();

        // Lấy danh sách đơn vị cấp dưới dựa trên ID của đơn vị hiện tại của người muốn xem thông tin báo cáo
        List<ObjectId> accessibleUnits = unitService.getAccessibleUnits(account.getUnit().getId());

        //Đơn vị muốn lấy báo cáo nằm trong cây đơn vị mà user này quản lý
        if(!accessibleUnits.contains(account.getUnit().getId())){
            throw new HttpException(10003, "Users are not allowed.", HttpServletResponse.SC_FORBIDDEN);
        }

        consumptionList = consumptionRepository.findByUnitIdIn(accessibleUnits);

        //Lưu trữ tổng điện nước theo từng cấp
        Map<ObjectId, ConsumptionTotalDto> consumptionSummaryMap = new HashMap<>();

        for(Consumption consumption: consumptionList){
            ObjectId currentUnitId = consumption.getUnit().getId();
            int unitLevel = unitService.getUnitLevel(currentUnitId);
            long electricityNumber = consumption.getElectricityConsumption().getNumber();
            long waterNumber = consumption.getWaterConsumption().getNumber();

            while (currentUnitId != null && unitLevel >= currentLevel) {
                consumptionSummaryMap.putIfAbsent(currentUnitId, new ConsumptionTotalDto(0, 0));
                ConsumptionTotalDto summary = consumptionSummaryMap.get(currentUnitId);

                // Cộng dồn điện nước
                summary.setTotalElectricityConsumption(summary.getTotalElectricityConsumption() + electricityNumber);
                summary.setTotalWaterConsumption(summary.getTotalWaterConsumption() + waterNumber);

                Unit unit = unitService.findUnitById(currentUnitId);
                // Cập nhật đơn vị cấp cao hơn
                if(Objects.nonNull(unit.getParentUnit())){
                    currentUnitId = unit.getParentUnit().getId();
                    unitLevel = unit.getParentUnit().getUnitLevel();
                }else{
                    currentUnitId = null;
                }
            }
        }

        // Thêm tổng hợp điện nước cho các đơn vị cấp cao hơn vào kết quả
        consumptionSummaryMap.forEach((id, summary) -> {
            ConsumptionTotalDto consumptionTotalDto = new ConsumptionTotalDto();
            // Tìm hoặc tạo `ConsumptionDetailResponseDto` cho từng cấp
            Unit unit = unitService.findUnitById(id);
            com.project.EWCM.pojo.Unit unitDto = new com.project.EWCM.pojo.Unit(unit.getId(),unit.getUnitName(), unit.getUnitLevel());

            consumptionTotalDto.setUnit(unitDto);
            consumptionTotalDto.setTotalElectricityConsumption(summary.getTotalElectricityConsumption());
            consumptionTotalDto.setTotalWaterConsumption(summary.getTotalWaterConsumption());
            consumptionTotalDtoList.add(consumptionTotalDto);
        });

        logger.info("EWCM-Get Consumption Total: " + consumptionTotalDtoList.toString());
        return consumptionTotalDtoList;
    }
}
