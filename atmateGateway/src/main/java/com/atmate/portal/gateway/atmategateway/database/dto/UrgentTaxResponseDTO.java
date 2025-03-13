package com.atmate.portal.gateway.atmategateway.database.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrgentTaxResponseDTO {
    private Integer clientId;
    private String clientName;
    private LocalDate nextPaymentDate; //data mais proxima de todos os impostos encontrados
    private List<TaxDetail> taxes;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TaxDetail {
        private Integer taxId;
        private String taxData;
        private Integer type;
        private String licensePlate;
        private String amount;
        private LocalDate paymentDeadline;
        private Long daysLeft;
    }
}