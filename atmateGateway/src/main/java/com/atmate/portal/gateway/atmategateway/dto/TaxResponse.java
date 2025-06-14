package com.atmate.portal.gateway.atmategateway.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxResponse {

    String identificadorUnico;
    String tipo;
    LocalDate dataLimite;
    String clientName;
    String valor;
    String estado;
    String json;

}
