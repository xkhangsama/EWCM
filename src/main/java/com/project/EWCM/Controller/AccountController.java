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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest request, @RequestBody @Valid LoginRequestDto loginRequest){
        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        logger.info("EWCD-Request: " + requestPath);
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponseDto(jwt,"Bearer",
                    userDetails.getUsername(), roles));
        } catch (BadCredentialsException e) {
            throw new HttpException(10006,"Tài khoản hoặc mật khẩu không đúng.", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            throw new HttpException(10005,"Đã có lỗi xảy ra, vui lòng thử lại.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
