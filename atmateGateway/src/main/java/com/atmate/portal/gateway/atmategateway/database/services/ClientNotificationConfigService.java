package com.atmate.portal.gateway.atmategateway.database.services;

import com.atmate.portal.gateway.atmategateway.dto.UpdateNotificationConfigRequest;
import com.atmate.portal.gateway.atmategateway.database.entitites.ClientNotificationConfig;
import com.atmate.portal.gateway.atmategateway.database.entitites.ContactType;
import com.atmate.portal.gateway.atmategateway.database.entitites.TaxType;
import com.atmate.portal.gateway.atmategateway.database.repos.ClientNotificationConfigRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClientNotificationConfigService {

    @Autowired
    private ClientNotificationConfigRepository clientNotificationConfigRepository;
    @Autowired
    private ContactTypeService contactTypeService;
    @Autowired
    private TaxTypeService taxTypeService;
    @Autowired
    private ClientNotificationService clientNotificationService;

    // Criar uma nova configuração de notificação do cliente
    public ClientNotificationConfig createClientNotificationConfig(ClientNotificationConfig clientNotificationConfig) {
        return clientNotificationConfigRepository.save(clientNotificationConfig);
    }

    // Ler todas as configurações de notificação do cliente
    public List<ClientNotificationConfig> getAllClientNotificationConfigs() {
        return clientNotificationConfigRepository.findAll();
    }

    // Ler uma configuração de notificação do cliente por ID
    public Optional<ClientNotificationConfig> getClientNotificationConfigById(Integer id) {
        return clientNotificationConfigRepository.findById(id);
    }

    // Atualizar uma configuração de notificação do cliente
    // Método de atualização
    @Transactional // Garante que a operação é atômica
    public ClientNotificationConfig updateClientNotificationConfig(Integer id, UpdateNotificationConfigRequest dto) {
        // 1. Busca a configuração existente ou lança exceção
        ClientNotificationConfig existingConfig = clientNotificationConfigRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ClientNotificationConfig not found with id: " + id)); // Ou ResourceNotFoundException

        // 2. Busca as entidades relacionadas (NotificationType e TaxType)
        // Trate o caso de IDs nulos no DTO, se permitido
        ContactType notificationType = null;
        if (dto.getNotificationTypeId() != null) {
            notificationType = contactTypeService.getContactTypeById(dto.getNotificationTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("NotificationType not found with id: " + dto.getNotificationTypeId())); // Ou ResourceNotFoundException
        }

        TaxType taxType = null;
        if (dto.getTaxTypeId() != null) {
            taxType = taxTypeService.getTaxTypeById(dto.getTaxTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("TaxType not found with id: " + dto.getTaxTypeId())); // Ou ResourceNotFoundException
        }

        // 3. Atualiza os campos da entidade existente
        existingConfig.setNotificationType(notificationType);
        existingConfig.setTaxType(taxType);
        existingConfig.setFrequency(dto.getFrequency());
        existingConfig.setStartPeriod((byte) dto.getStartPeriod()); // Assume que DTO e entidade usam int
        existingConfig.setActive(dto.getActive());       // Assume que DTO e entidade usam boolean

        // IMPORTANTE: Não atualizamos o 'client' aqui, pois não veio no DTO
        // e geralmente não se muda o cliente de uma configuração existente desta forma.

        // 4. Salva a entidade atualizada
        return clientNotificationConfigRepository.save(existingConfig);
    }

    @Transactional // Garante que a operação é atômica
    public ClientNotificationConfig updateClientNotificationConfig(Integer id, boolean active) {
        // 1. Busca a configuração existente ou lança exceção
        ClientNotificationConfig existingConfig = clientNotificationConfigRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ClientNotificationConfig not found with id: " + id)); // Ou ResourceNotFoundException

        existingConfig.setActive(active);

        return clientNotificationConfigRepository.save(existingConfig);
    }

    // Deletar uma configuração de notificação do cliente
    public boolean deleteClientNotificationConfig(Integer id) {
        if (!clientNotificationConfigRepository.existsById(id)) {
            throw new RuntimeException("Configuração de notificação do cliente não encontrada com ID: " + id);
        }

        if(clientNotificationService.existsClientNotificationsByConfigurationId(id)){
            clientNotificationService.deleteAllNotificationsByConfigurationId(id);
        }

        clientNotificationConfigRepository.deleteById(id);

        return !clientNotificationConfigRepository.existsById(id);
    }

    @Transactional
    public void deleteClientNotificationConfigByClientId(Integer id) {
        if (!clientNotificationConfigRepository.existsClientNotificationConfigByClientId(id)) {
            System.out.println("Configuração de notificação não encontrada com o ID: " + id);
        }

        clientNotificationConfigRepository.deleteClientNotificationConfigByClientId(id);
    }



}
