package com.project.EWCM.Controller;

import com.project.EWCM.DTO.AccountDto;
import com.project.EWCM.DTO.IdDto;
import com.project.EWCM.DTO.JwtResponseDto;
import com.project.EWCM.DTO.LoginRequestDto;
import com.project.EWCM.Service.AccountService;
import com.project.EWCM.config.jwt.JwtUtils;
import com.project.EWCM.config.services.UserDetailsImpl;
import com.project.EWCM.exception.HttpException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    Logger logger = LoggerFactory.getLogger(AccountController.class);
    @PostMapping("/register")
    public ResponseEntity<Object> createAccount(HttpServletRequest request, @RequestBody @Valid AccountDto accountDto){
        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        logger.info("EWCD-Request: " + requestPath);

        IdDto newAccount = accountService.createAccount(accountDto);
        return ResponseEntity.ok(newAccount);
    }

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(HttpException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest request, @RequestBody @Valid LoginRequestDto loginRequest){
        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        logger.info("EWCD-Request: " + requestPath);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        List<String> roles = userDetails.getAuthorities().stream()
//                .map(item -> item.getAuthority())
//                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponseDto(jwt,"Bearer",
                userDetails.getUsername()));
    }
}
