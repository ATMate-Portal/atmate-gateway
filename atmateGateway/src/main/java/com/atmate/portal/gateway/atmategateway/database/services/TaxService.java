package com.atmate.portal.gateway.atmategateway.database.services;

import com.atmate.portal.gateway.atmategateway.database.dto.TaxResponseDTO;
import com.atmate.portal.gateway.atmategateway.database.dto.UrgentTaxResponseDTO;
import com.atmate.portal.gateway.atmategateway.database.entitites.Client;
import com.atmate.portal.gateway.atmategateway.database.entitites.Tax;
import com.atmate.portal.gateway.atmategateway.database.repos.TaxRepository;
import com.atmate.portal.gateway.atmategateway.utils.enums.ErrorEnum;
import com.atmate.portal.gateway.atmategateway.utils.exceptions.ATMateException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
public class TaxService {

    @Autowired
    private TaxRepository taxRepository;
    @Autowired
    private ObjectMapper objectMapper;

    // Criar um novo imposto
    public Tax createTax(Tax tax) {
        return taxRepository.save(tax);
    }

    // Ler todos os impostos
    public List<Tax> getAllTaxes() {
        return taxRepository.findAll();
    }
    // Ler todos os impostos
    public List<Tax> getTaxesByClient(Client client) {
        return taxRepository.getTaxesByClient(client);
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

    public List<TaxResponseDTO> getTaxes(){

        List<TaxResponseDTO> taxList = new ArrayList<>();
        List<Tax> taxes = taxRepository.findAll();

        for (Tax tax : taxes) {
            tax.validateData();

            try {

                JsonNode jsonNode = objectMapper.readTree(tax.getTaxData());

                String identifier = tax.getIdentifier(jsonNode);
                String amount = tax.getAmount(jsonNode);
                String state = tax.getState(jsonNode);

                if (identifier == null || amount == null || state == null) {
                    throw new ATMateException(ErrorEnum.INVALID_JSON_STRUCTURE);
                }

                TaxResponseDTO taxResponse = new TaxResponseDTO();
                taxResponse.setIdentificadorUnico(identifier);
                taxResponse.setTipo(tax.getTaxType().getDescription());
                taxResponse.setDataLimite(tax.getPaymentDeadline());

                taxResponse.setValor(amount.trim());
                taxResponse.setEstado(state);
                taxResponse.setClientName(tax.getClient().getName());
                taxResponse.setJson(tax.getTaxData());

                taxList.add(taxResponse);
            } catch (JsonProcessingException e) {
                throw new ATMateException(ErrorEnum.INVALID_JSON);
            }

        }

        return taxList;
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
            tax.validateData();

            try {
                JsonNode jsonNode = objectMapper.readTree(tax.getTaxData());

                String identifier = tax.getIdentifier(jsonNode);
                String amount = tax.getAmount(jsonNode);
                String state = tax.getState(jsonNode);

                if("Pago".equals(state)){
                    continue;
                }

                if (identifier == null || amount == null) {
                    throw new ATMateException(ErrorEnum.INVALID_JSON_STRUCTURE);
                }

                // Criar um objeto do imposto individual
                UrgentTaxResponseDTO.TaxDetail taxDetail = UrgentTaxResponseDTO.TaxDetail.builder()
                        .taxId(tax.getId())
                        .taxData(tax.getTaxData())
                        .type(tax.getTaxType().getDescription())
                        .licensePlate(identifier)
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

            } catch (JsonProcessingException e) {
                throw new ATMateException(ErrorEnum.INVALID_JSON);
            }
        }

        // Criar lista a partir do mapa de clientes
        List<UrgentTaxResponseDTO> result = new ArrayList<>(clientTaxesMap.values());

        // Definir corretamente o nextPaymentDate com base no imposto mais próximo
        for (UrgentTaxResponseDTO responseDTO : result) {
            responseDTO.setNextPaymentDate(
                    responseDTO.getTaxes().stream()
                            .map(UrgentTaxResponseDTO.TaxDetail::getPaymentDeadline)
                            .min(LocalDate::compareTo)
                            .orElse(null)
            );
        }

        // Ordenar impostos dentro de cada cliente por daysLeft
        for (UrgentTaxResponseDTO responseDTO : result) {
            responseDTO.getTaxes().sort(
                    Comparator.comparing(UrgentTaxResponseDTO.TaxDetail::getDaysLeft)
            );
        }

        // Ordenar clientes por nextPaymentDate e clientName
        result.sort(Comparator.comparing(UrgentTaxResponseDTO::getNextPaymentDate)
                .thenComparing(UrgentTaxResponseDTO::getClientName));

        return result;

    }

    @Transactional
    public void deleteTaxByClientId(Integer id) {
        if (!taxRepository.existsTaxByClientId(id)) {
            System.out.println("Taxa não encontrada com o ID: " + id);
        }

        taxRepository.deleteTaxByClientId(id);
    }




}
