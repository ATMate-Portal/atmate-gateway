package com.atmate.portal.gateway.atmategateway.controller;

import com.atmate.portal.gateway.atmategateway.database.dto.CreateNotificationConfigRequestDTO;
import com.atmate.portal.gateway.atmategateway.database.dto.OperationHistoryRequestDTO;
import com.atmate.portal.gateway.atmategateway.database.dto.UpdateNotificationConfigRequestDTO;
import com.atmate.portal.gateway.atmategateway.database.entitites.*;
import com.atmate.portal.gateway.atmategateway.database.services.*;
import com.atmate.portal.gateway.atmategateway.services.IntegrationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
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
    ClientNotificationService clientNotificationService;
    @Autowired
    ClientService clientService;
    @Autowired
    TaxTypeService taxTypeService;
    @Autowired
    ContactTypeService contactTypeService;
    @Autowired
    OperationHistoryService operationHistoryService;
    @Autowired
    IntegrationClient integrationClient;

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

            OperationHistoryRequestDTO operationHistoryRequestDTO = new OperationHistoryRequestDTO();
            operationHistoryRequestDTO.setActionCode("CHECK-006");
            operationHistoryRequestDTO.setContextParameter(String.valueOf(configs.size()));
            operationHistoryService.createOperationHistory(operationHistoryRequestDTO);

            return ResponseEntity.ok().body(configs);
        } catch (Exception e) {
            log.error("Error fetching notification configurations: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null); // Return 500 Internal Server Error
        }
    }

    // Mantém o mapping original ou altera se fizer mais sentido (ex: /getNotificationsByIds)
    @GetMapping("/getNotifications")
    public ResponseEntity<List<ClientNotification>> getNotificationConfigsByIds(
            @RequestParam("ids") List<Integer> ids) {
        try {
            // Atualiza o log para indicar quais IDs estão a ser pedidos
            log.info("A obter configurações de notificação para os IDs: {}", ids);

            List<ClientNotification> configs = clientNotificationService.getClientNotificationsByConfigsId(ids);

            if (configs.isEmpty()) {
                // Atualiza o log para indicar que não foram encontradas configs para os IDs específicos
                log.info("Nenhuma configuração de notificação encontrada para os IDs fornecidos: {}", ids);
                // Retorna 200 OK com lista vazia, como no original
                return ResponseEntity.ok().body(configs);
            }

            // Atualiza o log de sucesso
            log.info("Recuperadas com sucesso {} configurações de notificação para os IDs: {}.", configs.size(), ids);

            // Atualiza o histórico de operações
            OperationHistoryRequestDTO operationHistoryRequestDTO = new OperationHistoryRequestDTO();
            operationHistoryRequestDTO.setActionCode("CHECK-007"); // Talvez um novo código de ação? (Opcional)
            // O parâmetro de contexto pode incluir os IDs pedidos e/ou quantos foram encontrados
            operationHistoryRequestDTO.setContextParameter(String.valueOf(configs.size()));
            operationHistoryService.createOperationHistory(operationHistoryRequestDTO);

            return ResponseEntity.ok().body(configs);
        } catch (Exception e) {
            // Atualiza o log de erro para incluir os IDs que causaram o problema
            log.error("Erro ao obter configurações de notificação para os IDs {}: {}", ids, e.getMessage(), e);
            // Mantém a resposta de erro 500
            return ResponseEntity.status(500).body(null);
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
                            clientNotificationConfig.setActive(requestBody.isActive());
                            clientNotificationConfig.setStartPeriod((byte) requestBody.getStartPeriod());

                            ClientNotificationConfig savedConfig = clientNotificationConfigService.createClientNotificationConfig(clientNotificationConfig);
                            clientNotificationConfigSavedList.add(savedConfig);
                            log.info("Successfully created notification configuration with id: {}", savedConfig.getId()); // Assuming getId() exists

                        }

                    }



            }

            OperationHistoryRequestDTO operationHistoryRequestDTO = new OperationHistoryRequestDTO();
            operationHistoryRequestDTO.setActionCode("ADD-002");
            operationHistoryRequestDTO.setContextParameter(String.valueOf(clientNotificationConfigSavedList.size()));
            operationHistoryService.createOperationHistory(operationHistoryRequestDTO);

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
     * @param updatedConfigDTO The updated notification configuration data from the request body.
     * @return ResponseEntity containing the updated configuration, a not found status, or an error status.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<ClientNotificationConfig> updateNotificationConfig(
            @PathVariable Integer id,
            @RequestBody UpdateNotificationConfigRequestDTO updatedConfigDTO) { // <-- USA O NOVO DTO
        try {
            log.info("Attempting to update notification configuration with id: {}", id);

            // A lógica de chamar o serviço pode ser simplificada se o serviço lançar exceção
            // em caso de não encontrado, mas vamos manter a verificação Optional aqui por enquanto.
            Optional<ClientNotificationConfig> existingConfigOpt = clientNotificationConfigService.getClientNotificationConfigById(id);

            if (existingConfigOpt.isPresent()) {
                // Passa o DTO correto para o método de serviço
                ClientNotificationConfig savedConfig = clientNotificationConfigService.updateClientNotificationConfig(id, updatedConfigDTO);
                log.info("Successfully updated notification configuration with id: {}", id);

                OperationHistoryRequestDTO operationHistoryRequestDTO = new OperationHistoryRequestDTO();
                operationHistoryRequestDTO.setActionCode("UPD-002");
                operationHistoryRequestDTO.setContextParameter(String.valueOf(existingConfigOpt.get().getId()));
                operationHistoryService.createOperationHistory(operationHistoryRequestDTO);

                return ResponseEntity.ok(savedConfig);
            } else {
                log.warn("Notification configuration with id: {} not found for update.", id);
                return ResponseEntity.notFound().build(); // 404 Not Found
            }

        } catch (ResourceNotFoundException rnfe) { // Exemplo: Exceção específica se ID de tipo/imposto não existir
            log.warn("Resource not found during update for config id {}: {}", id, rnfe.getMessage());
            return ResponseEntity.badRequest().body(null); // Ou retornar uma mensagem de erro
        } catch (Exception e) {
            log.error("Error updating notification configuration with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 Internal Server Error
        }
    }

    @PutMapping("/update/{id}/status")
    public ResponseEntity<ClientNotificationConfig> updateNotificationAtiveConfig(
            @PathVariable Integer id,
            @RequestParam boolean active) {
        try {
            log.info("Attempting to update notification configuration with id: {}", id);

            // A lógica de chamar o serviço pode ser simplificada se o serviço lançar exceção
            // em caso de não encontrado, mas vamos manter a verificação Optional aqui por enquanto.
            Optional<ClientNotificationConfig> existingConfigOpt = clientNotificationConfigService.getClientNotificationConfigById(id);

            if (existingConfigOpt.isPresent()) {
                // Passa o DTO correto para o método de serviço
                ClientNotificationConfig savedConfig = clientNotificationConfigService.updateClientNotificationConfig(id, active);
                log.info("Successfully updated notification configuration with id: {}", id);

                OperationHistoryRequestDTO operationHistoryRequestDTO = new OperationHistoryRequestDTO();
                operationHistoryRequestDTO.setActionCode("UPD-001");
                operationHistoryRequestDTO.setContextParameter(String.valueOf(existingConfigOpt.get().getId()));
                operationHistoryService.createOperationHistory(operationHistoryRequestDTO);


                return ResponseEntity.ok(savedConfig);
            } else {
                log.warn("Notification configuration with id: {} not found for update.", id);
                return ResponseEntity.notFound().build(); // 404 Not Found
            }

        } catch (ResourceNotFoundException rnfe) { // Exemplo: Exceção específica se ID de tipo/imposto não existir
            log.warn("Resource not found during update for config id {}: {}", id, rnfe.getMessage());
            return ResponseEntity.badRequest().body(null); // Ou retornar uma mensagem de erro
        } catch (Exception e) {
            log.error("Error updating notification configuration with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 Internal Server Error
        }
    }

    // --- NOVO MÉTODO DELETE ---
    /**
     * Endpoint to delete an existing notification configuration.
     *
     * @param id The ID of the configuration to delete (from path variable).
     * @return ResponseEntity indicating success (204 No Content), not found (404), or error (500).
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteNotificationConfig(@PathVariable Integer id) {
        try {
            log.info("Attempting to delete notification configuration with id: {}", id);

            // Assume-se que o serviço deleteClientNotificationConfig(id) existe
            // e que pode lançar uma exceção se o ID não for encontrado.
            // Uma exceção comum para isto é EmptyResultDataAccessException do Spring Data.

            boolean deleted = clientNotificationConfigService.deleteClientNotificationConfig(id); // Modificado para retornar boolean

            if (deleted) {
                log.info("Successfully deleted notification configuration with id: {}", id);
                // Retorna 204 No Content em caso de sucesso.

                OperationHistoryRequestDTO operationHistoryRequestDTO = new OperationHistoryRequestDTO();
                operationHistoryRequestDTO.setActionCode("DEL-002");
                operationHistoryRequestDTO.setContextParameter(String.valueOf(id));
                operationHistoryService.createOperationHistory(operationHistoryRequestDTO);



                return ResponseEntity.noContent().build();
            } else {
                log.warn("Notification configuration with id: {} not found for deletion.", id);
                // Se o serviço retornar false em vez de lançar exceção quando não encontra
                return ResponseEntity.notFound().build(); // 404 Not Found
            }

        }
        // Se o serviço lançar uma exceção personalizada:
        catch (ResourceNotFoundException e) {
            log.warn("Notification configuration with id: {} not found for deletion.", id);
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        catch (Exception e) {
            log.error("Error deleting notification configuration with id {}: {}", id, e.getMessage(), e);
            // Retorna 500 Internal Server Error para outras exceções inesperadas
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // --- FIM DO NOVO MÉTODO DELETE ---

    // --- NOVO MÉTODO PARA FORÇAR ENVIO ---
    /**
     * Endpoint to force the immediate dispatch of notifications for a specific configuration.
     *
     * @param configId The ID of the ClientNotificationConfig to trigger.
     * @return ResponseEntity indicating the outcome of the operation.
     */
    @PostMapping("/forceSend/{configId}")
    public ResponseEntity<?> forceSendNotification(@PathVariable Integer configId) {
        try {
            log.info("Attempting to force send notifications for configuration ID: {}", configId);

            // Passo 1: Verificar se a configuração existe e está ativa (ou outra lógica de negócio)
            Optional<ClientNotificationConfig> configOpt = clientNotificationConfigService.getClientNotificationConfigById(configId);
            if (configOpt.isEmpty()) {
                log.warn("Notification configuration with ID {} not found for force send.", configId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Configuração de notificação com ID " + configId + " não encontrada.");
            }

            ClientNotificationConfig config = configOpt.get();
            // if (!config.isActive()) {
            //     log.warn("Notification configuration with ID {} is inactive. Force send aborted.", configId);
            //     return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            //                          .body("A configuração de notificação com ID " + configId + " está inativa e não pode ser enviada manualmente.");
            // }

            int notificationsTriggeredCount = integrationClient.sendNotification(configId);

            if (notificationsTriggeredCount == 0) {
                log.info("No notifications were actively triggered for config ID {} (e.g., no matching clients found or already sent recently).", configId);
                // Mesmo que nada seja disparado, a operação de "forçar" foi tentada.
            } else {
                log.info("Successfully triggered {} notifications for configuration ID: {}", notificationsTriggeredCount, configId);
            }

            // Passo 3: Registar a operação no histórico
            OperationHistoryRequestDTO operationHistoryRequestDTO = new OperationHistoryRequestDTO();
            operationHistoryRequestDTO.setActionCode("FORCE-SEND-001"); // Novo código de ação
            operationHistoryRequestDTO.setContextParameter("ConfigID: " + configId + ", Triggered: " + notificationsTriggeredCount);
            operationHistoryService.createOperationHistory(operationHistoryRequestDTO);

            return ResponseEntity.ok().body("Processo de envio forçado para a configuração ID " + configId + " despoletou " + notificationsTriggeredCount + " notificação(ões).");

        } catch (ResourceNotFoundException e) { // Lançado pelo serviço se o configId não for encontrado
            log.warn("Resource not found during force send for config ID {}: {}", configId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) { // Exemplo: Se o serviço determinar que não pode forçar o envio (e.g. inativa)
            log.warn("Illegal state for forcing send for config ID {}: {}", configId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error forcing notification send for configuration ID {}: {}", configId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro inesperado ao tentar forçar o envio da notificação.");
        }
    }
}
