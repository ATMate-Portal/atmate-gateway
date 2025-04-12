package com.atmate.portal.gateway.atmategateway.controller;

import com.atmate.portal.gateway.atmategateway.database.dto.ClientAtCredentialDTO;
import com.atmate.portal.gateway.atmategateway.database.dto.ClientFilterDTO;
import com.atmate.portal.gateway.atmategateway.database.dto.ClientResponseDTO;
import com.atmate.portal.gateway.atmategateway.database.entitites.AtCredential;
import com.atmate.portal.gateway.atmategateway.database.entitites.Client;
import com.atmate.portal.gateway.atmategateway.database.entitites.User;
import com.atmate.portal.gateway.atmategateway.database.repos.AtCredentialRepository;
import com.atmate.portal.gateway.atmategateway.database.services.AtCredentialService;
import com.atmate.portal.gateway.atmategateway.database.services.ClientService;
import com.atmate.portal.gateway.atmategateway.database.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    //GET /clients
    @GetMapping
    public Page<ClientResponseDTO> getClients(
            ClientFilterDTO filter,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return clientService.getClients(filter, pageable);
    }
    //GET /clients/{id}
    //Descrição: detalhes de um cliente específico (para ver dados completos ou abrir modal).

    //GET /clients/types
    //Descrição: devolve lista de tipos de cliente (client_type) para preencher dropdowns.
}


