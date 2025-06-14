package com.atmate.portal.gateway.atmategateway.dto;

import lombok.Data;

@Data
public class CreateClientRequest {
    private Integer nif;
    private String password;
}
