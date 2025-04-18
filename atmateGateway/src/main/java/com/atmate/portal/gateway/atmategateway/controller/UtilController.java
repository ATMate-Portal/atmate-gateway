package com.atmate.portal.gateway.atmategateway.controller;

import com.atmate.portal.gateway.atmategateway.database.dto.ParamsDTO;
import com.atmate.portal.gateway.atmategateway.database.entitites.Configuration;
import com.atmate.portal.gateway.atmategateway.database.services.ConfigurationService;
import com.atmate.portal.gateway.atmategateway.services.CryptoService;
import com.atmate.portal.gateway.atmategateway.services.KeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@RestController
@RequestMapping("/util")
@Slf4j
public class UtilController {

    @Autowired
    private KeyService keyService;

    @GetMapping("/generateKey")
    public String generateKey() throws NoSuchAlgorithmException {
        keyService.generateAndStoreKey();
        return "Chave gerada e armazenada.";
    }




}
