package com.project.EWCM.Controller;

import com.project.EWCM.DTO.*;
import com.project.EWCM.Service.UnitService;
import com.project.EWCM.config.jwt.JwtUtils;
import com.project.EWCM.exception.HttpException;
import com.project.EWCM.pojo.Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unit")
public class UnitController {
    @Autowired
    private UnitService unitService;

    @Autowired
    private JwtUtils jwtUtils;

    Logger logger = LoggerFactory.getLogger(UnitController.class);
    @PostMapping("/create-unit")
    public ResponseEntity<Object> createUnit(HttpServletRequest request, @RequestBody UnitRequestDto unitDto){
        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        logger.info("EWCD-Request: " + requestPath);
        // Lấy token từ header Authorization
        String token = request.getHeader("Authorization").substring(7); // Lấy token sau "Bearer "

        // Lấy thông tin từ token
        String username = jwtUtils.getUserNameFromJwtToken(token);
        IdDto newUnit = unitService.createUnit(unitDto, username);
        return ResponseEntity.ok(newUnit);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUnitDetail(HttpServletRequest request, @PathVariable(value = "id", required = true) ObjectId id){
        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        logger.info("EWCD-Request: " + requestPath);
        // Lấy token từ header Authorization
        String token = request.getHeader("Authorization").substring(7); // Lấy token sau "Bearer "

        // Lấy thông tin từ token
        String username = jwtUtils.getUserNameFromJwtToken(token);
        UnitDto unitDetail = unitService.getUnitDetail(username, id);
        return ResponseEntity.ok(unitDetail);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUnit(HttpServletRequest request, @PathVariable(value = "id", required = true) ObjectId id, @RequestBody UnitUpdateRequestDto unitUpdateRequestDto){
        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        logger.info("EWCD-Request: " + requestPath);
        // Lấy token từ header Authorization
        String token = request.getHeader("Authorization").substring(7); // Lấy token sau "Bearer "

        // Lấy thông tin từ token
        String username = jwtUtils.getUserNameFromJwtToken(token);
        AffectedRowsDto affectedRows = unitService.updateUnit(unitUpdateRequestDto, username, id);
        return ResponseEntity.ok(affectedRows);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUnit(HttpServletRequest request, @PathVariable(value = "id", required = true) ObjectId id){
        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        logger.info("EWCD-Request: " + requestPath);
        // Lấy token từ header Authorization
        String token = request.getHeader("Authorization").substring(7); // Lấy token sau "Bearer "

        // Lấy thông tin từ token
        String username = jwtUtils.getUserNameFromJwtToken(token);
        AffectedRowsDto affectedRows = unitService.deleteUnit( username, id);
        return ResponseEntity.ok(affectedRows);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Object> updateUnitHead(HttpServletRequest request, @PathVariable(value = "id", required = true) ObjectId id, @RequestBody Account unitHead){
        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        logger.info("EWCD-Request: " + requestPath);
        // Lấy token từ header Authorization
        String token = request.getHeader("Authorization").substring(7); // Lấy token sau "Bearer "

        // Lấy thông tin từ token
        String username = jwtUtils.getUserNameFromJwtToken(token);
        AffectedRowsDto affectedRowsDto = unitService.updateUnitHead(username, id, unitHead);
        return ResponseEntity.ok(affectedRowsDto);
    }

    @PostMapping("/{id}/--add-user-into-unit")
    public ResponseEntity<Object> addUserIntoUnit(HttpServletRequest request, @PathVariable(value = "id", required = true) ObjectId id, @RequestBody Account user){
        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        logger.info("EWCD-Request: " + requestPath);
        // Lấy token từ header Authorization
        String token = request.getHeader("Authorization").substring(7); // Lấy token sau "Bearer "

        // Lấy thông tin từ token
        String username = jwtUtils.getUserNameFromJwtToken(token);
        AffectedRowsDto affectedRowsDto = unitService.addUserIntoUnit(username, id, user);
        return ResponseEntity.ok(affectedRowsDto);
    }

    @PostMapping("/{id}/--remove-user-from-unit")
    public ResponseEntity<Object> removeUserFromUnit(HttpServletRequest request, @PathVariable(value = "id", required = true) ObjectId id, @RequestBody Account user){
        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        logger.info("EWCD-Request: " + requestPath);
        // Lấy token từ header Authorization
        String token = request.getHeader("Authorization").substring(7); // Lấy token sau "Bearer "

        // Lấy thông tin từ token
        String username = jwtUtils.getUserNameFromJwtToken(token);
        AffectedRowsDto affectedRowsDto = unitService.removeUserFromUnit(username, id, user);
        return ResponseEntity.ok(affectedRowsDto);
    }

    @GetMapping()
    public ResponseEntity<Object> getAll(HttpServletRequest request){
        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        logger.info("EWCD-Request: " + requestPath);
        // Lấy token từ header Authorization
        String token = request.getHeader("Authorization").substring(7); // Lấy token sau "Bearer "

        List<String> roles = jwtUtils.getRolesFromJwtToken(token);
        // Kiểm tra xem người dùng có role admin không
        if (!roles.contains("admin")) {
            throw new HttpException(10003, "Forbidden: You do not have permission to access this resource", HttpServletResponse.SC_FORBIDDEN);
        }

        List<UnitDto> unitDtoList = unitService.getAll();
        return ResponseEntity.ok(unitDtoList);
    }

}
