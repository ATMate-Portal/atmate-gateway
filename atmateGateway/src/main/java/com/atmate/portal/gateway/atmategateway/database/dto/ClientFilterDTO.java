package com.atmate.portal.gateway.atmategateway.database.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ClientFilterDTO {
    private String name;
    private Integer nif;
    private Integer clientType;
    private String gender;
    private String nationality;
    private String colaborator;
    private LocalDate birthDateStart;
    private LocalDate birthDateEnd;
    private LocalDateTime createdAtStart;
    private LocalDateTime createdAtEnd;
}

