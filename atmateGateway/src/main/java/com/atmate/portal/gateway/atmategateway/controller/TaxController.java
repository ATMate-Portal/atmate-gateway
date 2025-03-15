package com.atmate.portal.gateway.atmategateway.controller;

import com.atmate.portal.gateway.atmategateway.database.dto.UrgentTaxResponseDTO;
import com.atmate.portal.gateway.atmategateway.database.services.TaxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tax")
public class TaxController {

    @Autowired
    TaxService taxService;

    @GetMapping("/getUrgentTaxes")
    public List<UrgentTaxResponseDTO> getUrgentTaxes(@RequestParam(value = "days", required = false) Integer days) {
        return taxService.getUrgentTaxes(days != null ? days : 14);
    }



}
