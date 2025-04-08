package com.atmate.portal.gateway.atmategateway.controller;

import com.atmate.portal.gateway.atmategateway.database.dto.ClientAtCredentialDTO;
import com.atmate.portal.gateway.atmategateway.database.dto.ClientInputCreateDTO;
import com.atmate.portal.gateway.atmategateway.database.entitites.AtCredential;
import com.atmate.portal.gateway.atmategateway.database.entitites.Client;
import com.atmate.portal.gateway.atmategateway.database.entitites.ClientType;
import com.atmate.portal.gateway.atmategateway.database.services.AtCredentialService;
import com.atmate.portal.gateway.atmategateway.database.services.ClientService;
import com.atmate.portal.gateway.atmategateway.services.IntegrationClient;
import com.atmate.portal.gateway.atmategateway.utils.enums.ErrorEnum;
import com.atmate.portal.gateway.atmategateway.utils.exceptions.ATMateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients")
@Slf4j
public class ClientController {

    @Autowired
    ClientService clientService;
    @Autowired
    AtCredentialService atCredentialService;
    @Autowired
    IntegrationClient integrationClient;


    @PostMapping("/create")
    public ResponseEntity<Client> createUser(@RequestBody ClientAtCredentialDTO clientAtCredentialDTO) throws Exception {

        Client client = clientService.createClient(clientAtCredentialDTO.getClient());

        if (client != null) {
            AtCredential atCredential = clientAtCredentialDTO.getAtCredential();
            atCredential.setClient(client);

            AtCredential newATCredential = atCredentialService.createAtCredential(atCredential);

            if (newATCredential != null) {
                return new ResponseEntity<>(client, HttpStatus.CREATED);
            }
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/create2")
    public ResponseEntity<Client> createUser2(@RequestBody ClientInputCreateDTO input) throws Exception {
        String nif = String.valueOf(input.getNif());
        //Check NIF
        if (nif == null || !nif.matches("\\d{9}")) {
            throw new ATMateException(ErrorEnum.INVALID_NIF);
        }

        //Check duplicated client
        if (clientService.existsByNif(input.getNif())) {
            throw new ATMateException(ErrorEnum.CLIENT_ALREADY_EXISTS);
        }

        Client client = new Client();
        client.setNif(input.getNif());
        client.setName("PENDING");
        client.setClientType(new ClientType(4, null, null, null)); //TODO terminar logica

        Client clientSaved = clientService.createClient(client);

        if (clientSaved != null) {
            AtCredential atCredential = new AtCredential();
            atCredential.setPassword(input.getPassword());
            atCredential.setClient(clientSaved);

            AtCredential newATCredential = atCredentialService.createAtCredential(atCredential);

            if (newATCredential != null) {
                integrationClient.syncClient(client.getId());
                return new ResponseEntity<>(client, HttpStatus.CREATED);
            }
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}