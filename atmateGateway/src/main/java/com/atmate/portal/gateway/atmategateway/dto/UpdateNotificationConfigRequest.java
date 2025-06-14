package com.atmate.portal.gateway.atmategateway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationConfigRequest {
    // Corresponde ao 'notificationTypeId' do frontend
    private Integer notificationTypeId;

    // Corresponde ao 'taxTypeId' do frontend
    private Integer taxTypeId;

    // Campos restantes que o frontend envia
    private String frequency;
    private int startPeriod; // Usar Integer para permitir null se necessário, mas int está ok se for sempre obrigatório
    private Boolean active;    // Usar Boolean para permitir null se necessário, mas boolean está ok se for sempre obrigatório
}
