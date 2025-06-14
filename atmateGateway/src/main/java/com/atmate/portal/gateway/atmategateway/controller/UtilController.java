package com.atmate.portal.gateway.atmategateway.controller;


import com.atmate.portal.gateway.atmategateway.services.KeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/util")
@Slf4j
@Tag(name = "Utilidades")
public class UtilController {

    @Autowired
    private KeyService keyService;

    @GetMapping("/generateKey")
    @Operation(
            summary = "Gerar uma chave simétrica",
            description = "Endpoint que gera uma chave simétrica"
    )
    public String generateKey() throws NoSuchAlgorithmException {
        keyService.generateAndStoreKey();
        return "Chave gerada e armazenada.";
    }




}
