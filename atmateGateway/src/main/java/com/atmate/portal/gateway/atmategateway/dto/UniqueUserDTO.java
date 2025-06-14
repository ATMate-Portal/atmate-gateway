package com.atmate.portal.gateway.atmategateway.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UniqueUserDTO {
    private final Integer userId;
    private final String username;
}
