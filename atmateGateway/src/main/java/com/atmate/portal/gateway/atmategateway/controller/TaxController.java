package com.atmate.portal.gateway.atmategateway.controller;

import com.atmate.portal.gateway.atmategateway.database.dto.*;
import com.atmate.portal.gateway.atmategateway.database.entitites.Configuration;
import com.atmate.portal.gateway.atmategateway.database.entitites.TaxType;
import com.atmate.portal.gateway.atmategateway.database.services.ConfigurationService;
import com.atmate.portal.gateway.atmategateway.database.services.OperationHistoryService;
import com.atmate.portal.gateway.atmategateway.database.services.TaxService;
import com.atmate.portal.gateway.atmategateway.database.services.TaxTypeService;
import com.atmate.portal.gateway.atmategateway.dto.OperationHistoryRequest;
import com.atmate.portal.gateway.atmategateway.dto.TaxResponse;
import com.atmate.portal.gateway.atmategateway.dto.TaxTypeResponse;
import com.atmate.portal.gateway.atmategateway.dto.UrgentTaxResponse;
import com.atmate.portal.gateway.atmategateway.utils.enums.ErrorEnum;
import com.atmate.portal.gateway.atmategateway.utils.exceptions.ATMateException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*" /*${cors.allowed.origin}"*/) // Corrected line
@RequestMapping("/tax")
@Slf4j
@Tag(name = "Gestão de impostos")
public class TaxController {

    @Autowired
    TaxService taxService;
    @Autowired
    TaxTypeService taxTypeService;
    @Autowired
    OperationHistoryService operationHistoryService;
    @Autowired
    ConfigurationService configurationService;

    @Value("${warning.days.value:warning_days}")
    private String warningDaysVarName;
    @GetMapping("/getUrgentTaxes")
    @Operation(
            summary = "Obter impostos urgentes dos clientes",
            description = "Endpoint que retorna todos os impostos mais urgentes (dependendo dos parâmetros)"
    )
    public List<UrgentTaxResponse> getUrgentTaxes(@RequestParam Integer days) {

        List<UrgentTaxResponse> urgentTaxResponseDTOList = taxService.getUrgentTaxes(days);

        Optional<Configuration> configuration = configurationService.getConfigurationString(warningDaysVarName);

        String param = configuration.map(Configuration::getVarvalue).orElse(null);

        if(!Objects.equals(param, String.valueOf(days))){
            throw new ATMateException(ErrorEnum.GENERIC_ERROR);
        }

        OperationHistoryRequest operationHistoryRequestDTO = new OperationHistoryRequest();
        operationHistoryRequestDTO.setActionCode("CHECK-001");
        operationHistoryRequestDTO.setContextParameter(String.valueOf(urgentTaxResponseDTOList.size()));
        operationHistoryService.createOperationHistory(operationHistoryRequestDTO);

        return urgentTaxResponseDTOList;
    }

    @GetMapping("/getTaxes")
    @Operation(
            summary = "Obter impostos dos clientes",
            description = "Endpoint que retorna todos os impostos dos clientes"
    )
    public List<TaxResponse> getTaxes() {

        List<TaxResponse> taxResponseDTOList =  taxService.getTaxes();

        OperationHistoryRequest operationHistoryRequestDTO = new OperationHistoryRequest();
        operationHistoryRequestDTO.setActionCode("CHECK-002");
        operationHistoryRequestDTO.setContextParameter(String.valueOf(taxResponseDTOList.size()));
        operationHistoryService.createOperationHistory(operationHistoryRequestDTO);

        return taxResponseDTOList;
    }

    @GetMapping("/getTypes")
    @Operation(
            summary = "Obter tipos de impostos",
            description = "Endpoint que retorna todos os tipos de impostos dos clientes"
    )
    public List<TaxTypeResponse> getTypes() {

        List<TaxType> taxTypes = taxTypeService.getAllTaxTypes();
        List<TaxTypeResponse> taxTypeResponseList = new ArrayList<>();

        for ( TaxType taxType : taxTypes){
            TaxTypeResponse taxTypeResponse = new TaxTypeResponse(taxType.getId(), taxType.getDescription());
            taxTypeResponseList.add(taxTypeResponse);
        }

        return taxTypeResponseList;
    }



}
