package com.project.EWCM.Service;

import com.project.EWCM.DTO.*;
import com.project.EWCM.Document.Account;
import com.project.EWCM.Document.Unit;
import com.project.EWCM.exception.HttpException;
import com.project.EWCM.repository.AccountRepository;
import com.project.EWCM.repository.UnitRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Service
public class UnitService {
    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Value("${project.app.maxLevel}")
    private int maxLevel;

    Logger logger = LoggerFactory.getLogger(UnitService.class);

    public IdDto createUnit(UnitRequestDto unitDto, String username) {
        Account account = accountService.findAccountByUsername(username);
        int levelOfUnitAccount = getAccountUnitLevel(account);
        Date currentDate = new Date();
        //Xử lý nếu quyền admin thì tạo đơn vị cấp lớn nhất (cấp 1) còn không thì tạo đơn vị cấp tiếp theo của đơn vị hiện tại của người tạo
        if(!account.getType().equals("admin")){
            validateAccountCanCreateUnit(account,levelOfUnitAccount);
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
        Account accountHead = new Account();
        if(Objects.nonNull(unitDto.getUnitHead())){
            newUnit.setUnitHead(unitDto.getUnitHead());
            accountHead = accountRepository.findById(unitDto.getUnitHead().getId()).orElse(null);
        }
        if(Objects.nonNull(unitDto.getAccountListOfUnit())){
            newUnit.setAccountListOfUnit(unitDto.getAccountListOfUnit());
        }
        if(Objects.nonNull(account.getUnit())){
            newUnit.setParentUnit(account.getUnit());
        }

        //Mapping dữ liệu tài khoản tạo
        com.project.EWCM.pojo.Account createdBy = new com.project.EWCM.pojo.Account();
        createdBy.setId(account.getId());
        createdBy.setUsername(account.getUsername());
        createdBy.setEmail(account.getEmail());
        createdBy.setFullName(account.getFullName());
        createdBy.setType(account.getType());

        newUnit.setCreatedBy(createdBy);
        newUnit.setCreatedDate(currentDate);
        newUnit.setUpdatedDate(currentDate);

        // set unit level = current unit level +1
        newUnit.setUnitLevel(levelOfUnitAccount + 1);
        unitRepository.save(newUnit);
        // set unit for account head
        com.project.EWCM.pojo.Unit unitOfAccount = new com.project.EWCM.pojo.Unit(newUnit.getId(), newUnit.getUnitName(), newUnit.getUnitLevel());
        accountHead.setUnit(unitOfAccount);
        accountHead.setHead(true);
        accountRepository.save(accountHead);
//        updateAccountHead(accountHead.getId(), newUnit);

        logger.info("EWCM-Saved Unit Data: " + newUnit.toString());
        return new IdDto(newUnit.getId());
    }

    // Hàm phụ để lấy cấp đơn vị hiện tại của tài khoản
    private int getAccountUnitLevel(Account account) {
        return (Objects.nonNull(account.getUnit()) && Objects.nonNull(account.getUnit().getUnitLevel()))
                ? account.getUnit().getUnitLevel()
                : 0;
    }

    // Hàm phụ để kiểm tra xem tài khoản có quyền tạo đơn vị hay không
    private void validateAccountCanCreateUnit(Account account, int levelOfUnitAccount) {
        if (!account.isHead()) {
            throw new HttpException(10003, "Users are not allowed to create units", HttpServletResponse.SC_BAD_REQUEST);
        }

        if (levelOfUnitAccount >= maxLevel) {
            throw new HttpException(10003, "Users are not allowed to create units of a lower level.", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    public AffectedRowsDto updateUnit(UnitUpdateRequestDto unitUpdateRequestDto, String username, ObjectId id) {
        AffectedRowsDto affectedRowsDto = new AffectedRowsDto();
        Account account = accountRepository.findByUsername(username).orElseThrow(() ->
                new HttpException(10004, "User not found.", HttpServletResponse.SC_NOT_FOUND)
        );

        Unit oldUnit = unitRepository.findById(id).get();
        //Chỉ cập nhật được thông tin đơn vị do mình tạo ra hoặc mình là trưởng đơn vị
        if (!oldUnit.getCreatedBy().getId().equals(account.getId()) && !oldUnit.getUnitHead().getId().equals(account.getId())) {
            throw new HttpException(10003, "Users are not allowed.", HttpServletResponse.SC_FORBIDDEN);
        }
        Date currentDate = new Date();
        if(Objects.nonNull(unitUpdateRequestDto.getUnitName())){
            oldUnit.setUnitName(unitUpdateRequestDto.getUnitName());
        }
        if(Objects.nonNull(unitUpdateRequestDto.getUnitAddress())){
            oldUnit.setUnitAddress(unitUpdateRequestDto.getUnitAddress());
        }
        if(Objects.nonNull(unitUpdateRequestDto.getUnitNumber())){
            oldUnit.setUnitNumber(unitUpdateRequestDto.getUnitNumber());
        }
        if(Objects.nonNull(unitUpdateRequestDto.getUnitPhoneNumber())){
            oldUnit.setUnitPhoneNumber(unitUpdateRequestDto.getUnitPhoneNumber());
        }
        if(Objects.nonNull(unitUpdateRequestDto.getUnitHead())){
            oldUnit.setUnitHead(unitUpdateRequestDto.getUnitHead());
        }
        if(Objects.nonNull(unitUpdateRequestDto.getAccountListOfUnit())){
            oldUnit.setAccountListOfUnit(unitUpdateRequestDto.getAccountListOfUnit());
        }

        oldUnit.setCreatedDate(currentDate);
        oldUnit.setUpdatedDate(currentDate);

        com.project.EWCM.pojo.Account updatedBy =
                new com.project.EWCM.pojo.Account(account.getId(),account.getUsername(),account.getFullName(), account.getEmail(), account.getType());
        oldUnit.setUpdatedBy(updatedBy);

        Unit updatedUnit = unitRepository.save(oldUnit);
        affectedRowsDto.setAffectedRows(1);
        logger.info("EWCM-Update Unit Data: " + updatedUnit.toString());
        return affectedRowsDto;
    }

    public AffectedRowsDto deleteUnit(String username, ObjectId id) {
        AffectedRowsDto affectedRows = new AffectedRowsDto();
        Unit oldUnit = unitRepository.findById(id).orElse(null);
        if(oldUnit != null){
            unitRepository.deleteById(id);
            affectedRows.setAffectedRows(1);
        }else{
            affectedRows.setAffectedRows(0);
            affectedRows.setError("Unit not found.");
        }
        logger.info("EWCM-Delete Unit Data: " + oldUnit.toString());
        return affectedRows;
    }

    public UnitDto getUnitDetail(String username, ObjectId id) {
        try {
            Unit unitDetail = findUnitById(id);

            Account account = accountRepository.findByUsername(username).orElseThrow(() ->
                    new HttpException(10004, "User not found.", HttpServletResponse.SC_NOT_FOUND)
            );

            // Kiểm tra quyền truy cập
            if (unitDetail.getUnitHead() == null || unitDetail.getCreatedBy() == null) {
                throw new HttpException(10003, "Unit head or creator not defined.", HttpServletResponse.SC_FORBIDDEN);
            }

            //Chỉ lấy thông tin chi tiết đơn vị mà người dùng tạo hoặc người dùng quản lý
            if (!unitDetail.getUnitHead().getId().equals(account.getId()) &&
                    !unitDetail.getCreatedBy().getId().equals(account.getId())) {
                throw new HttpException(10003, "Users are not allowed.", HttpServletResponse.SC_FORBIDDEN);
            }

            // Tạo DTO
            UnitDto result = mapToUnitDto(unitDetail);
            logger.info("EWCM-Get Unit Detail: " + result.toString());
            return result;

        } catch (HttpException e) {
            logger.info("EWCM-Get Unit Detail: " + e.getMessage());
            throw new HttpException(10005, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }


    public AffectedRowsDto updateUnitHead(String username, ObjectId id, com.project.EWCM.pojo.Account unitHead) {
        AffectedRowsDto affectedRowsDto = new AffectedRowsDto();
        Unit unit = findUnitById(id);

        Account account = accountRepository.findByUsername(username).orElseThrow(() ->
                new HttpException(10004, "User not found.", HttpServletResponse.SC_NOT_FOUND)
        );

        // Chỉ set được trưởng đơn vị của đơn vị do mình tạo ra
        if (!unit.getCreatedBy().getId().equals(account.getId())) {
            throw new HttpException(10003, "Users are not allowed.", HttpServletResponse.SC_FORBIDDEN);
        }

        unit.setUnitHead(unitHead);
        unitRepository.save(unit);
        updateAccountHead(unitHead.getId(), unit);
//        Account accountHead = getAccountById(unitHead.getId());
//        accountHead.setHead(true);
//        com.project.EWCM.pojo.Unit unitOfAccount = new com.project.EWCM.pojo.Unit(unit.getId(), unit.getUnitName(), unit.getUnitLevel());
//        accountHead.setUnit(unitOfAccount);
//        accountRepository.save(accountHead);
        affectedRowsDto.setAffectedRows(1);
        logger.info("EWCM-Update Unit Data: " + unit.toString());
        return affectedRowsDto;
    }

    private void updateAccountHead(ObjectId accountId, Unit unit) {
        Account accountHead = getAccountById(accountId);
        accountHead.setHead(true);
        com.project.EWCM.pojo.Unit unitOfAccount = new com.project.EWCM.pojo.Unit(unit.getId(), unit.getUnitName(), unit.getUnitLevel());
        accountHead.setUnit(unitOfAccount);
        accountRepository.save(accountHead);
    }

    public AffectedRowsDto addUserIntoUnit(String username, ObjectId id, com.project.EWCM.pojo.Account user) {
        AffectedRowsDto affectedRowsDto = new AffectedRowsDto();
        Unit unit = findUnitById(id);

        Account account = accountRepository.findByUsername(username).orElseThrow(() ->
                new HttpException(10004, "User not found.", HttpServletResponse.SC_NOT_FOUND)
        );
        // Chỉ thêm người dùng vào đơn vị do mình tạo ra va account chưa thuộc đơn vị nào
        if (!unit.getCreatedBy().getId().equals(account.getId()) && !unit.getUnitHead().getId().equals(account.getId())) {
            throw new HttpException(10003, "Users are not allowed.", HttpServletResponse.SC_FORBIDDEN);
        }
        Account userAdd = getAccountById(user.getId());
        if(Objects.nonNull(userAdd.getUnit())){
            throw new HttpException(10007, "User already belongs to another unit.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if(Objects.nonNull(unit.getAccountListOfUnit())) {
            List<com.project.EWCM.pojo.Account> accountList = unit.getAccountListOfUnit();
            accountList.add(user);
            unit.setAccountListOfUnit(accountList);
        }else{
            List<com.project.EWCM.pojo.Account> accountList = new ArrayList<>();
            accountList.add(user);
            unit.setAccountListOfUnit(accountList);
        }
        unitRepository.save(unit);
        com.project.EWCM.pojo.Unit unitOfAccount = new com.project.EWCM.pojo.Unit(unit.getId(), unit.getUnitName(), unit.getUnitLevel());
        userAdd.setUnit(unitOfAccount);
        accountRepository.save(userAdd);
        affectedRowsDto.setAffectedRows(1);
        logger.info("EWCM-Update Unit Data: " + unit.toString());
        return affectedRowsDto;
    }

    public AffectedRowsDto removeUserFromUnit(String username, ObjectId id, com.project.EWCM.pojo.Account user) {
        AffectedRowsDto affectedRowsDto = new AffectedRowsDto();
        Unit unit = findUnitById(id);

        Account account = accountRepository.findByUsername(username).orElseThrow(() ->
                new HttpException(10004, "User not found.", HttpServletResponse.SC_NOT_FOUND)
        );

        // Chỉ xóa người dùng khỏi đơn vị do mình tạo ra
        if (!unit.getCreatedBy().getId().equals(account.getId()) && !unit.getUnitHead().getId().equals(account.getId())) {
            throw new HttpException(10003, "Users are not allowed.", HttpServletResponse.SC_FORBIDDEN);
        }
        List<com.project.EWCM.pojo.Account> accountList = unit.getAccountListOfUnit();
        if(Objects.isNull(accountList)){
            throw new HttpException(10003, "Account list is null", HttpServletResponse.SC_BAD_REQUEST);
        }

        accountList.removeIf(acc -> acc.getId().equals(user.getId()));
        unit.setAccountListOfUnit(accountList);
        unitRepository.save(unit);

        Account userRemove = getAccountById(user.getId());
        userRemove.setUnit(null);
        accountRepository.save(userRemove);
        affectedRowsDto.setAffectedRows(1);
        logger.info("EWCM-Update Unit Data: " + unit.toString());
        return affectedRowsDto;
    }

    public Unit findUnitById(ObjectId id) {
        return unitRepository.findById(id)
                .orElseThrow(() -> new HttpException(10004, "Unit not found.", HttpServletResponse.SC_NOT_FOUND));
    }

    private Account getAccountById(ObjectId id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new HttpException(10004, "User not found.", HttpServletResponse.SC_NOT_FOUND));
    }

    private UnitDto mapToUnitDto(Unit unitDetail) {
        UnitDto result = new UnitDto();
        result.setUnitName(unitDetail.getUnitName());
        result.setUnitAddress(unitDetail.getUnitAddress());
        result.setUnitNumber(unitDetail.getUnitNumber());
        result.setUnitPhoneNumber(unitDetail.getUnitPhoneNumber());
        result.setUnitLevel(unitDetail.getUnitLevel());
        result.setUnitHead(unitDetail.getUnitHead());
        result.setAccountListOfUnit(unitDetail.getAccountListOfUnit());
        return result;
    }

    public List<UnitDto> getAll() {
        List<Unit> unitList = unitRepository.findAll();
        List<UnitDto> result = unitRepository.findAll().stream()
                .map(unit -> {
                    UnitDto dto = new UnitDto();
                    dto.setUnitName(unit.getUnitName());
                    dto.setUnitAddress(unit.getUnitAddress());
                    dto.setUnitNumber(unit.getUnitNumber());
                    dto.setUnitPhoneNumber(unit.getUnitPhoneNumber());
                    dto.setUnitLevel(unit.getUnitLevel());
                    dto.setUnitHead(unit.getUnitHead());
                    dto.setAccountListOfUnit(unit.getAccountListOfUnit());
                    return dto;
                })
                .collect(Collectors.toList());
        return result;
    }

    public List<Unit> findByParentUnitId(ObjectId parentUnitId) {
        List<Unit> unitList = new ArrayList<>();
        unitList = unitRepository.findByParentUnitId(parentUnitId);
        return unitList;
    }

    public List<ObjectId> getAccessibleUnits(ObjectId accountUnitId) {
        List<Unit> accessibleUnits = new ArrayList<>();
        Queue<Unit> queue = new LinkedList<>();

        Unit currentUnit = findUnitById(accountUnitId);
        queue.add(currentUnit);
        // Sử dụng BFS để duyệt qua tất cả các đơn vị cấp dưới
        while (!queue.isEmpty()) {
            Unit unit = queue.poll();
            accessibleUnits.add(unit);
            List<Unit> subUnits = findByParentUnitId(unit.getId());  // Bổ sung thêm parentUnitId cho Unit collection
            queue.addAll(subUnits);
        }
        // Tạo danh sách chứa ID của các đơn vị cấp dưới
        List<ObjectId> accessibleUnitIds = accessibleUnits.stream()
                .map(Unit::getId)
                .collect(Collectors.toList());
        return accessibleUnitIds;
    }

    public int getUnitLevel(ObjectId unitId) {
        return findUnitById(unitId).getUnitLevel();
    }
}
