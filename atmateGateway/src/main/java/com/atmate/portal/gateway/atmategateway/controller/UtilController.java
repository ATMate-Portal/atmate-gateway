package com.atmate.portal.gateway.atmategateway.controller;

import com.atmate.portal.gateway.atmategateway.services.CryptoService;
import com.atmate.portal.gateway.atmategateway.services.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/util")
public class UtilController {

    @Autowired
    private KeyService keyService;

    @GetMapping("/generateKey")
    public String generateKey() throws NoSuchAlgorithmException {
        keyService.generateAndStoreKey();
        return "Chave gerada e armazenada.";
    }

}
