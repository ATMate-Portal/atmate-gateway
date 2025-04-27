package com.atmate.portal.gateway.atmategateway.controller;

import com.atmate.portal.gateway.atmategateway.database.dto.*;
import com.atmate.portal.gateway.atmategateway.database.entitites.Configuration;
import com.atmate.portal.gateway.atmategateway.database.entitites.Tax;
import com.atmate.portal.gateway.atmategateway.database.entitites.TaxType;
import com.atmate.portal.gateway.atmategateway.database.services.ConfigurationService;
import com.atmate.portal.gateway.atmategateway.database.services.OperationHistoryService;
import com.atmate.portal.gateway.atmategateway.database.services.TaxService;
import com.atmate.portal.gateway.atmategateway.database.services.TaxTypeService;
import com.atmate.portal.gateway.atmategateway.utils.enums.ErrorEnum;
import com.atmate.portal.gateway.atmategateway.utils.exceptions.ATMateException;
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
    public List<UrgentTaxResponseDTO> getUrgentTaxes(@RequestParam Integer days) {

        List<UrgentTaxResponseDTO> urgentTaxResponseDTOList = taxService.getUrgentTaxes(days);

        Optional<Configuration> configuration = configurationService.getConfigurationString(warningDaysVarName);

        String param = configuration.map(Configuration::getVarvalue).orElse(null);

        if(!Objects.equals(param, String.valueOf(days))){
            throw new ATMateException(ErrorEnum.GENERIC_ERROR);
        }

        OperationHistoryRequestDTO operationHistoryRequestDTO = new OperationHistoryRequestDTO();
        operationHistoryRequestDTO.setActionCode("CHECK-001");
        operationHistoryRequestDTO.setContextParameter(String.valueOf(urgentTaxResponseDTOList.size()));
        operationHistoryService.createOperationHistory(operationHistoryRequestDTO);

        return urgentTaxResponseDTOList;
    }

    @GetMapping("/getTaxes")

    public List<TaxResponseDTO> getTaxes() {

        List<TaxResponseDTO> taxResponseDTOList =  taxService.getTaxes();

        OperationHistoryRequestDTO operationHistoryRequestDTO = new OperationHistoryRequestDTO();
        operationHistoryRequestDTO.setActionCode("CHECK-002");
        operationHistoryRequestDTO.setContextParameter(String.valueOf(taxResponseDTOList.size()));
        operationHistoryService.createOperationHistory(operationHistoryRequestDTO);

        return taxResponseDTOList;
    }

    @GetMapping("/getTypes")
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
