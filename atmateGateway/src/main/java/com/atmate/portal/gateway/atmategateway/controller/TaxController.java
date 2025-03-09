package com.atmate.portal.gateway.atmategateway.controller;

import com.atmate.portal.gateway.atmategateway.database.entitites.Tax;
import com.atmate.portal.gateway.atmategateway.database.services.TaxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tax")
public class TaxController {

    @Autowired
    TaxService taxService;

    @GetMapping("/getUrgentTaxes")
    public List<Tax> getUrgentTaxes() {
        return taxService.getAllTaxes();
    }


}
