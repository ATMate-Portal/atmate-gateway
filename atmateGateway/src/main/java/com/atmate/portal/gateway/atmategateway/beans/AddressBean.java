package com.atmate.portal.gateway.atmategateway.beans;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AddressBean {
    private String street;
    private String doorNumber;
    private String zipCode;
    private String city;
    private String county;
    private String district;
    private String parish;
    private String country;
    private String addressTypeName; // nome do tipo de morada (ex: Faturação, Residência)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
