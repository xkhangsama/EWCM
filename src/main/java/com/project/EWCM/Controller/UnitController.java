package com.project.EWCM.Controller;

import com.project.EWCM.DTO.AccountDto;
import com.project.EWCM.DTO.IdDto;
import com.project.EWCM.DTO.UnitDto;
import com.project.EWCM.DTO.UnitRequestDto;
import com.project.EWCM.Service.UnitService;
import com.project.EWCM.config.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
