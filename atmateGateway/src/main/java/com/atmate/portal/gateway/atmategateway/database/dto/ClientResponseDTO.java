package com.atmate.portal.gateway.atmategateway.database.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ClientResponseDTO {
    private Integer id;
    private String name;
    private Integer nif;
    private String gender;
    private String nationality;
    private String associatedColaborator;
    private LocalDate birthDate;
    private LocalDateTime lastRefreshDate;

}