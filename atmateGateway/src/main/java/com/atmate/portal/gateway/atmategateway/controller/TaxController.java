package com.atmate.portal.gateway.atmategateway.controller;

import com.atmate.portal.gateway.atmategateway.database.dto.OperationHistoryRequestDTO;
import com.atmate.portal.gateway.atmategateway.database.dto.TaxResponseDTO;
import com.atmate.portal.gateway.atmategateway.database.dto.UrgentTaxResponseDTO;
import com.atmate.portal.gateway.atmategateway.database.services.OperationHistoryService;
import com.atmate.portal.gateway.atmategateway.database.services.TaxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*" /*${cors.allowed.origin}"*/) // Corrected line
@RequestMapping("/tax")
public class TaxController {

    @Autowired
    TaxService taxService;

    @Autowired
    OperationHistoryService operationHistoryService;

    @GetMapping("/getUrgentTaxes")
    public List<UrgentTaxResponseDTO> getUrgentTaxes(@RequestParam Integer days) {

        List<UrgentTaxResponseDTO> urgentTaxResponseDTOList = taxService.getUrgentTaxes(days);

        OperationHistoryRequestDTO operationHistoryRequestDTO = new OperationHistoryRequestDTO();
        operationHistoryRequestDTO.setActionCode("CHECK-001");
        operationHistoryService.createOperationHistory(operationHistoryRequestDTO);

        return urgentTaxResponseDTOList;
    }

    @GetMapping("/getTaxes")

    public List<TaxResponseDTO> getTaxes() {

        List<TaxResponseDTO> taxResponseDTOList =  taxService.getTaxes();

        OperationHistoryRequestDTO operationHistoryRequestDTO = new OperationHistoryRequestDTO();
        operationHistoryRequestDTO.setActionCode("CHECK-002");
        operationHistoryService.createOperationHistory(operationHistoryRequestDTO);

        return taxResponseDTOList;
    }



}
