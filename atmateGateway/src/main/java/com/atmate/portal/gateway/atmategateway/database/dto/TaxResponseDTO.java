package com.atmate.portal.gateway.atmategateway.database.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxResponseDTO {

    String identificadorUnico;
    String tipo;
    LocalDate dataLimite;
    String clientName;
    String valor;
    String estado;
    String json;

}
