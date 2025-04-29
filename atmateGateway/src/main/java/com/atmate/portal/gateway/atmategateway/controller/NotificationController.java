package com.atmate.portal.gateway.atmategateway.controller;

import com.atmate.portal.gateway.atmategateway.database.dto.CreateNotificationConfigRequestDTO;
import com.atmate.portal.gateway.atmategateway.database.entitites.*;
import com.atmate.portal.gateway.atmategateway.database.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@CrossOrigin(origins = "*" /*${cors.allowed.origin}"*/) // Corrected line
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    ClientNotificationConfigService clientNotificationConfigService;
    @Autowired
    ClientService clientService;
    @Autowired
    TaxTypeService taxTypeService;
    @Autowired
    ContactTypeService contactTypeService;

    @GetMapping("/getNotificationConfig")
    public ResponseEntity<List<ClientNotificationConfig>> getNotificationConfigs() {
        try {
            log.info("Fetching notification configurations");
            List<ClientNotificationConfig> configs = clientNotificationConfigService.getAllClientNotificationConfigs();
            if (configs.isEmpty()) {
                log.info("No notification configurations found.");
                return ResponseEntity.ok().body(configs); // Return empty list with 200 OK
            }
            log.info("Successfully retrieved {} notification configurations.", configs.size());
            return ResponseEntity.ok().body(configs);
        } catch (Exception e) {
            log.error("Error fetching notification configurations: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null); // Return 500 Internal Server Error
        }
    }

    /**
     * Endpoint to create a new notification configuration.
     *
     * @param requestBody The notification configuration data from the request body.
     * @return ResponseEntity containing the created configuration or an error status.
     */
    @PostMapping("/create")
    public ResponseEntity createNotificationConfig(@RequestBody CreateNotificationConfigRequestDTO requestBody) {
        try {
            log.info("Attempting to create new notification configuration");
            // Assuming service returns the saved entity with ID populated
            List<ClientNotificationConfig> clientNotificationConfigSavedList = new ArrayList<>();


            List<Integer> clientList = requestBody.getClientsIDs();
            List<Integer> taxTypeList = requestBody.getTaxTypeIDs();
            List<Integer> notificationTypeList = requestBody.getNotificationTypeList();

            for(Integer clientID : clientList) {

                    for (Integer taxTypeID : taxTypeList){

                        for (Integer notificationType : notificationTypeList){
                            ClientNotificationConfig clientNotificationConfig = new ClientNotificationConfig();

                            Optional<Client> client = clientService.getClientById(clientID);

                            if (client.isPresent()) {
                                clientNotificationConfig.setClient(client.get());
                            } else {
                                log.info("Não foi encontrado o cliente com o ID: " + clientID + " a continuar com a criação das restantes notificações.");
                            }

                            Optional<TaxType> taxType = taxTypeService.getTaxTypeById(taxTypeID);
                            if (taxType.isPresent()){
                                clientNotificationConfig.setTaxType(taxType.get());
                            }else{
                                log.info("Não foi encontrado o imposto com o ID: " + taxTypeID + " a continuar com a criação das restantes notificações.");
                                continue;
                            }

                            Optional<ContactType> contactType = contactTypeService.getContactTypeById(notificationType);

                            if (contactType.isPresent()){
                                clientNotificationConfig.setNotificationType(contactType.get());
                            }else{
                                log.info("Não foi encontrado o tipo de notificação com o ID: " + notificationType + " a continuar com a criação das restantes notificações.");
                                continue;
                            }

                            clientNotificationConfig.setFrequency(requestBody.getFrequency());
                            clientNotificationConfig.setIsActive(Boolean.valueOf(requestBody.getIsActive()));
                            clientNotificationConfig.setStartPeriod((byte) requestBody.getStartPeriod());

                            ClientNotificationConfig savedConfig = clientNotificationConfigService.createClientNotificationConfig(clientNotificationConfig);
                            clientNotificationConfigSavedList.add(savedConfig);
                            log.info("Successfully created notification configuration with id: {}", savedConfig.getId()); // Assuming getId() exists

                        }

                    }



            }

            // Return 201 Created status with the created object in the body
            return ResponseEntity.status(HttpStatus.CREATED).body(clientNotificationConfigSavedList);
        } catch (Exception e) {
            // Catch more specific exceptions if possible (e.g., DataIntegrityViolationException)
            log.error("Error creating notification configuration: {}", e.getMessage(), e);
            // Consider returning a more specific error DTO instead of null
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Endpoint to update an existing notification configuration.
     *
     * @param id            The ID of the configuration to update (from path variable).
     * @param updatedConfig The updated notification configuration data from the request body.
     * @return ResponseEntity containing the updated configuration, a not found status, or an error status.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<ClientNotificationConfig> updateNotificationConfig(
            @PathVariable Integer id,
            @RequestBody ClientNotificationConfig updatedConfig) {
        try {
            log.info("Attempting to update notification configuration with id: {}", id);

            Optional<ClientNotificationConfig> clientNotificationConfig = clientNotificationConfigService.getClientNotificationConfigById(id);

            if (clientNotificationConfig.isPresent()) {
                log.info("Successfully updated notification configuration with id: {}", id);
                ClientNotificationConfig savedConfig = clientNotificationConfigService.updateClientNotificationConfig(id, updatedConfig);
                return ResponseEntity.ok(savedConfig);
            } else {
                log.warn("Notification configuration with id: {} not found for update.", id);
                return ResponseEntity.notFound().build(); // Return 404 Not Found
            }

        } catch (Exception e) {
            log.error("Error updating notification configuration with id {}: {}", id, e.getMessage(), e);
            // Consider returning a more specific error DTO instead of null
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
