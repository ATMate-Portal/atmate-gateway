package com.atmate.portal.gateway.atmategateway.database.entitites;

import com.atmate.portal.gateway.atmategateway.utils.enums.ErrorEnum;
import com.atmate.portal.gateway.atmategateway.utils.exceptions.ATMateException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "taxes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Tax {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "tax_type", nullable = false)
    private TaxType taxType;

    @Column(name = "tax_data", columnDefinition = "json")
    private String taxData; // Se necessário, pode ser um objeto que representa o JSON

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "payment_deadline")
    private LocalDate paymentDeadline;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void validateData(){

        if (client == null) {
            throw new ATMateException(ErrorEnum.INVALID_TAX_CLIENT);
        }

        if (paymentDeadline == null) {
            throw new ATMateException(ErrorEnum.INVALID_TAX_DEADLINE_DATE);
        }

        if (taxData== null || taxData.isBlank()) {
            throw new ATMateException(ErrorEnum.INVALID_TAX_DATA);
        }
    }

    public String getIdentifier(JsonNode jsonNode) {
        return switch (taxType.getId()) {
            case 1 -> jsonNode.path("Matrícula").asText(); //IUC
            case 5 -> jsonNode.path("Nº Nota Cob.").asText(); //IMI
            default -> throw new ATMateException(ErrorEnum.INVALID_TAX_TYPE);
        };
    }

    public String getAmount(JsonNode jsonNode){

        String amount;

        switch (taxType.getId()) {
            case 1 -> amount = jsonNode.path("Valor Base").asText(); //IUC
            case 5 -> amount = jsonNode.path("Valor").asText(); //IMI
            default -> throw new ATMateException(ErrorEnum.INVALID_TAX_TYPE);
        }

        if(amount.contains("EUR")){
            amount = amount.replace("EUR",  "") + "€";
        }

        return amount;
    }

    public String getState(JsonNode jsonNode){

        String state;

        switch (taxType.getId()) {
            case 1 -> state = jsonNode.path("Situação da Nota").asText(); //IUC
            case 5 -> state = jsonNode.path("Situação").asText(); //IMI
            default -> throw new ATMateException(ErrorEnum.INVALID_TAX_TYPE);
        }

        if(state.equals("-")){
            state = "Pendente";
        }

        if(state.contains("Paga")){
            state = "Pago";
        }

        if(state.contains("Anulada")) {
            state = "Anulada";
        }

        return state;
    }

}

