package com.atmate.portal.gateway.atmategateway.database.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ContactDTO {
    private String contactTypeName; // nome do tipo de contato (ex: Email, Telemóvel)
    private String contact;
    private Boolean isDefaultContact;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
