package com.project.EWCM.Controller;

import com.project.EWCM.DTO.*;
import com.project.EWCM.Service.ConsumptionService;
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
@RequestMapping("/api/consumption")
public class ConsumptionController {
    @Autowired
    private ConsumptionService consumptionService;
    @Autowired
    private JwtUtils jwtUtils;
    Logger logger = LoggerFactory.getLogger(ConsumptionController.class);

    @PostMapping("/create-consumption")
    public ResponseEntity<Object> createConsumption(HttpServletRequest request, @RequestBody @Valid ConsumptionRequestDto consumptionRequestDto){
        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        logger.info("EWCM-Request: " + requestPath);
        // Lấy token từ header Authorization
        String token = request.getHeader("Authorization").substring(7); // Lấy token sau "Bearer "

        // Lấy thông tin từ token
        String username = jwtUtils.getUserNameFromJwtToken(token);
        IdDto newConsumption = consumptionService.createConsumption(consumptionRequestDto, username);
        return ResponseEntity.ok(newConsumption);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getConsumptionDetail(HttpServletRequest request, @PathVariable(value = "id", required = true) ObjectId id){
        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        logger.info("EWCM-Request: " + requestPath);
        // Lấy token từ header Authorization
        String token = request.getHeader("Authorization").substring(7); // Lấy token sau "Bearer "

        // Lấy thông tin từ token
        String username = jwtUtils.getUserNameFromJwtToken(token);
        ConsumptionDetailResponseDto consumptionDetail = consumptionService.getConsumptionDetail(username, id);
        return ResponseEntity.ok(consumptionDetail);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateConsumption(HttpServletRequest request,
                                                    @PathVariable(value = "id", required = true) ObjectId id,
                                                    @RequestBody ConsumptionUpdateRequestDto consumptionUpdateRequestDto){
        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        logger.info("EWCM-Request: " + requestPath);
        // Lấy token từ header Authorization
        String token = request.getHeader("Authorization").substring(7); // Lấy token sau "Bearer "

        // Lấy thông tin từ token
        String username = jwtUtils.getUserNameFromJwtToken(token);
        AffectedRowsDto affectedRows = consumptionService.updateConsumption(consumptionUpdateRequestDto, username, id);
        return ResponseEntity.ok(affectedRows);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteConsumption(HttpServletRequest request, @PathVariable(value = "id", required = true) ObjectId id){
        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        logger.info("EWCM-Request: " + requestPath);
        // Lấy token từ header Authorization
        String token = request.getHeader("Authorization").substring(7); // Lấy token sau "Bearer "

        // Lấy thông tin từ token
        String username = jwtUtils.getUserNameFromJwtToken(token);
        AffectedRowsDto affectedRows = consumptionService.deleteConsumption( username, id);
        return ResponseEntity.ok(affectedRows);
    }
}
