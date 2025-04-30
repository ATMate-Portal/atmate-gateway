package com.atmate.portal.gateway.atmategateway.database.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ClientInfoResponseDTO {
    private Integer id;
    private String name;
    private Integer nif;
    private String gender;
    private String nationality;
    private String associatedColaborator;
    private LocalDate birthDate;
    private LocalDateTime lastRefreshDate;

    private List<AddressDTO> addresses;
    private List<ContactDTO> contacts;
    private List<TaxResponseDTO> taxes;
}

