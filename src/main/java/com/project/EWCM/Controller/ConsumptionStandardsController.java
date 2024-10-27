package com.project.EWCM.Controller;

import com.project.EWCM.DTO.*;
import com.project.EWCM.Service.ConsumptionService;
import com.project.EWCM.Service.ConsumptionStandardsService;
import com.project.EWCM.config.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consumption-standards")
public class ConsumptionStandardsController {
    @Autowired
    private ConsumptionStandardsService consumptionStandardsService;
    @Autowired
    private JwtUtils jwtUtils;
    Logger logger = LoggerFactory.getLogger(ConsumptionController.class);

    @PostMapping("/create")
    public ResponseEntity<Object> createConsumption(HttpServletRequest request, @RequestBody @Valid ConsumptionStandardsDto consumptionStandardsDto){
        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        logger.info("EWCM-Request: " + requestPath);
        // Lấy token từ header Authorization
        String token = request.getHeader("Authorization").substring(7); // Lấy token sau "Bearer "

        // Lấy thông tin từ token
        String username = jwtUtils.getUserNameFromJwtToken(token);
        IdDto newConsumption = consumptionStandardsService.createConsumptionStandards(consumptionStandardsDto, username);
        return ResponseEntity.ok(newConsumption);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getConsumptionStandardsDetail(HttpServletRequest request, @PathVariable(value = "id", required = true) ObjectId id){
        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        logger.info("EWCM-Request: " + requestPath);
//        // Lấy token từ header Authorization
//        String token = request.getHeader("Authorization").substring(7); // Lấy token sau "Bearer "
//
//        // Lấy thông tin từ token
//        String username = jwtUtils.getUserNameFromJwtToken(token);
        ConsumptionStandardResponseDto responseDto = consumptionStandardsService.getConsumptionStandardsDetail(id);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateConsumptionStandards(HttpServletRequest request,
                                                    @PathVariable(value = "id", required = true) ObjectId id,
                                                    @RequestBody ConsumptionStandardsUpdateDto updateDto){
        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        logger.info("EWCM-Request: " + requestPath);
        // Lấy token từ header Authorization
        String token = request.getHeader("Authorization").substring(7); // Lấy token sau "Bearer "

        // Lấy thông tin từ token
        String username = jwtUtils.getUserNameFromJwtToken(token);
        AffectedRowsDto affectedRows = consumptionStandardsService.updateConsumptionStandards(updateDto, username, id);
        return ResponseEntity.ok(affectedRows);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteConsumptionStandards(HttpServletRequest request, @PathVariable(value = "id", required = true) ObjectId id){
        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        logger.info("EWCM-Request: " + requestPath);
        // Lấy token từ header Authorization
        String token = request.getHeader("Authorization").substring(7); // Lấy token sau "Bearer "

        // Lấy thông tin từ token
        String username = jwtUtils.getUserNameFromJwtToken(token);
        AffectedRowsDto affectedRows = consumptionStandardsService.deleteConsumptionStandards( username, id);
        return ResponseEntity.ok(affectedRows);
    }
}
