package com.atmate.portal.gateway.atmategateway.dto;

import com.atmate.portal.gateway.atmategateway.beans.AddressBean;
import com.atmate.portal.gateway.atmategateway.beans.ContactBean;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ClientDetailsResponse {
    private Integer id;
    private String name;
    private Integer nif;
    private String gender;
    private String nationality;
    private String associatedColaborator;
    private LocalDate birthDate;
    private LocalDateTime lastRefreshDate;

    private List<AddressBean> addresses;
    private List<ContactBean> contacts;
    private List<TaxResponse> taxes;
    private List<NotificationClientResponse> notifications;
}

