package com.atmate.portal.gateway.atmategateway.database.dto;

import jakarta.validation.constraints.Null;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Null
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