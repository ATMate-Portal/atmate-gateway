package com.atmate.portal.gateway.atmategateway.controller;

import com.atmate.portal.gateway.atmategateway.database.dto.ClientAtCredentialDTO;
import com.atmate.portal.gateway.atmategateway.database.dto.ClientInputCreateDTO;
import com.atmate.portal.gateway.atmategateway.database.dto.ClientResponseDTO;
import com.atmate.portal.gateway.atmategateway.database.dto.OperationHistoryRequestDTO;
import com.atmate.portal.gateway.atmategateway.database.entitites.Address;
import com.atmate.portal.gateway.atmategateway.database.entitites.AtCredential;
import com.atmate.portal.gateway.atmategateway.database.entitites.Client;
import com.atmate.portal.gateway.atmategateway.database.entitites.ClientType;
import com.atmate.portal.gateway.atmategateway.database.services.*;
import com.atmate.portal.gateway.atmategateway.services.IntegrationClient;
import com.atmate.portal.gateway.atmategateway.utils.enums.ErrorEnum;
import com.atmate.portal.gateway.atmategateway.utils.exceptions.ATMateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    @Autowired
    AddressService addressService;
    @Autowired
    ContactService contactService;
    @Autowired
    TaxService taxService;
    @Autowired
    ClientTypeService clientTypeService;
    @Autowired
    OperationHistoryService operationHistoryService;

    @GetMapping("/getClients")
    public ResponseEntity<List<ClientResponseDTO>> getAllClients() {
        List<ClientResponseDTO> clients = clientService.getClients();

        OperationHistoryRequestDTO operationHistoryRequestDTO = new OperationHistoryRequestDTO();
        operationHistoryRequestDTO.setActionCode("CHECK-003");
        operationHistoryRequestDTO.setContextParameter(String.valueOf(clients.size()));
        operationHistoryService.createOperationHistory(operationHistoryRequestDTO);
        return ResponseEntity.ok(clients);
    }

    @PostMapping("/create")
    public ResponseEntity<Client> createUser(@RequestBody ClientInputCreateDTO input) throws Exception {
        String nif = String.valueOf(input.getNif());

        //Check NIF
        if (nif == null || !nif.matches("\\d{9}")) {
            throw new ATMateException(ErrorEnum.INVALID_NIF);
        }

        //Check duplicated client
        if (clientService.existsByNif(input.getNif())) {
            throw new ATMateException(ErrorEnum.CLIENT_ALREADY_EXISTS);
        }

        Optional<ClientType> clientType;
        boolean getTypeFromAT = false;

        if (nif.startsWith("6")) {
            clientType = clientTypeService.getClientTypeById(3);
        } else if (nif.startsWith("5")) {
            clientType = clientTypeService.getClientTypeById(2);
        } else if (nif.startsWith("1") || nif.startsWith("2") || nif.startsWith("3")) {
            clientType = clientTypeService.getClientTypeById(4);
            getTypeFromAT = true;
        } else {
            throw new ATMateException(ErrorEnum.CLIENT_TYPE_ERROR);
        }

        Client client = new Client(input.getNif(), clientType.orElse(null));
        Client clientSaved = clientService.createClient(client);

        if (clientSaved != null) {
            AtCredential atCredential = new AtCredential();
            atCredential.setPassword(input.getPassword());
            atCredential.setClient(clientSaved);

            AtCredential newATCredential = atCredentialService.createAtCredential(atCredential);

            if (newATCredential != null) {
                integrationClient.syncClient(client.getId(), getTypeFromAT);

                OperationHistoryRequestDTO operationHistoryRequestDTO = new OperationHistoryRequestDTO();
                operationHistoryRequestDTO.setActionCode("ADD-001");
                operationHistoryRequestDTO.setContextParameter(String.valueOf(client.getNif()));
                operationHistoryService.createOperationHistory(operationHistoryRequestDTO);

                return new ResponseEntity<>(client, HttpStatus.CREATED);
            }
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable Integer id) {
        Optional<Client> clientOptional = clientService.getClientById(id); // Procura o cliente pelo ID antes de eliminar

        if (clientOptional.isEmpty()) {
            return new ResponseEntity<>("Cliente não encontrado com o ID: " + id, HttpStatus.NOT_FOUND);
        }

        try {

            //delete all addresses
            addressService.deleteAddressByClientId(id);
            //delete credentials
            atCredentialService.deleteATCredentialByClientId(id);
            //delete contacts
            contactService.deleteContactByClientId(id);
            //delete tax information
            taxService.deleteTaxByClientId(id);
            //delete clientNotifications

            clientService.deleteClient(id);

            OperationHistoryRequestDTO operationHistoryRequestDTO = new OperationHistoryRequestDTO();
            operationHistoryRequestDTO.setActionCode("DEL-001");
            operationHistoryRequestDTO.setContextParameter(clientOptional.get().getName());
            operationHistoryService.createOperationHistory(operationHistoryRequestDTO);
            
            return new ResponseEntity<>("Cliente eliminado com sucesso.", HttpStatus.OK);
        } catch (Exception e) {
            // Log do erro para debugging
            e.printStackTrace();
            return new ResponseEntity<>("Erro ao eliminar o cliente com o ID: " + id + ". " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}