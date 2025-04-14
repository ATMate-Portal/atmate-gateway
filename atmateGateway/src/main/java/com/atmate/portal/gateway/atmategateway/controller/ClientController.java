package com.atmate.portal.gateway.atmategateway.controller;

import com.atmate.portal.gateway.atmategateway.database.dto.ClientAtCredentialDTO;
import com.atmate.portal.gateway.atmategateway.database.dto.ClientResponseDTO;
import com.atmate.portal.gateway.atmategateway.database.entitites.AtCredential;
import com.atmate.portal.gateway.atmategateway.database.entitites.Client;
import com.atmate.portal.gateway.atmategateway.database.entitites.User;
import com.atmate.portal.gateway.atmategateway.database.repos.AtCredentialRepository;
import com.atmate.portal.gateway.atmategateway.database.services.AtCredentialService;
import com.atmate.portal.gateway.atmategateway.database.services.ClientService;
import com.atmate.portal.gateway.atmategateway.database.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    ClientService clientService;
    @Autowired
    AtCredentialService atCredentialService;

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

    @GetMapping("/getClients")
    public ResponseEntity<List<ClientResponseDTO>> getAllClients() {
        List<ClientResponseDTO> clients = clientService.getClients();
        return ResponseEntity.ok(clients);
    }
}


