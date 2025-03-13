package com.atmate.portal.gateway.atmategateway.database.services;

import com.atmate.portal.gateway.atmategateway.database.dto.UrgentTaxResponseDTO;
import com.atmate.portal.gateway.atmategateway.database.entitites.Tax;
import com.atmate.portal.gateway.atmategateway.database.repos.TaxRepository;
import com.atmate.portal.gateway.atmategateway.utils.enums.ErrorEnum;
import com.atmate.portal.gateway.atmategateway.utils.exceptions.ATMateException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TaxService {
    private final TaxRepository taxRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public TaxService(TaxRepository taxRepository, ObjectMapper objectMapper) {
        this.taxRepository = taxRepository;
        this.objectMapper = objectMapper;
    }

    // Criar um novo imposto
    public Tax createTax(Tax tax) {
        return taxRepository.save(tax);
    }

    // Ler todos os impostos
    public List<Tax> getAllTaxes() {
        return taxRepository.findAll();
    }

    // Ler um imposto por ID
    public Optional<Tax> getTaxById(Integer id) {
        return taxRepository.findById(id);
    }

    // Atualizar um imposto
    public Tax updateTax(Integer id, Tax taxDetails) {
        Tax tax = taxRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imposto não encontrado com ID: " + id));

        // Atualizar os campos do imposto
        tax.setTaxType(taxDetails.getTaxType());
        tax.setTaxData(taxDetails.getTaxData());
        // Adicione outros campos conforme necessário

        return taxRepository.save(tax);
    }

    // Deletar um imposto
    public void deleteTax(Integer id) {
        if (!taxRepository.existsById(id)) {
            throw new RuntimeException("Imposto não encontrado com ID: " + id);
        }
        taxRepository.deleteById(id);
    }

    public List<UrgentTaxResponseDTO> getUrgentTaxes(int days) {
        log.info("getUrgentTaxes() Called");
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);

        List<Tax> taxes = taxRepository.findUrgentTaxes(today, futureDate);
        LinkedHashMap<Integer, UrgentTaxResponseDTO> clientTaxesMap = new LinkedHashMap<>();

        if(taxes.isEmpty()){
            log.info("getUrgentTaxes() returning empty list");
            return new ArrayList<>(clientTaxesMap.values());
        }

        for (Tax tax : taxes) {
            if (tax.getClient() == null) {
                throw new ATMateException(ErrorEnum.INVALID_TAX_CLIENT);
            }

            if (tax.getPaymentDeadline() == null) {
                throw new ATMateException(ErrorEnum.INVALID_TAX_DEADLINE_DATE);
            }

            if (tax.getTaxData() == null || tax.getTaxData().isBlank()) {
                throw new ATMateException(ErrorEnum.INVALID_TAX_DATA);
            }

            String licensePlate = null;
            String amount = null;

            try {
                JsonNode jsonNode = objectMapper.readTree(tax.getTaxData());
                licensePlate = jsonNode.path("Matrícula").asText();
                amount = jsonNode.path("Valor Base").asText();

                if (licensePlate == null || amount == null) {
                    throw new ATMateException(ErrorEnum.INVALID_JSON_STRUCTURE);
                }
            } catch (JsonProcessingException e) {
                throw new ATMateException(ErrorEnum.INVALID_JSON);
            }

            // Criar um objeto do imposto individual
            UrgentTaxResponseDTO.TaxDetail taxDetail = UrgentTaxResponseDTO.TaxDetail.builder()
                    .taxId(tax.getId())
                    .taxData(tax.getTaxData())
                    .type(tax.getTaxType().getId())
                    .licensePlate(licensePlate)
                    .amount(amount)
                    .paymentDeadline(tax.getPaymentDeadline())
                    .daysLeft(ChronoUnit.DAYS.between(LocalDate.now(), tax.getPaymentDeadline()))
                    .build();

            // Verificar se o cliente já está no mapa
            clientTaxesMap.computeIfAbsent(tax.getClient().getId(), clientId ->
                    UrgentTaxResponseDTO.builder()
                            .clientId(clientId)
                            .clientName(tax.getClient().getName())
                            .nextPaymentDate(tax.getPaymentDeadline())
                            .taxes(new ArrayList<>())
                            .build()
            ).getTaxes().add(taxDetail);
        }

        return new ArrayList<>(clientTaxesMap.values());
    }


}
