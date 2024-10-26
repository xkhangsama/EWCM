package com.project.EWCM.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDto {
    private String jwt;
    private String type = "Bearer";

    private Long jwtExpirationMs;

}
