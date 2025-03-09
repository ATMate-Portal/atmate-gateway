package com.atmate.portal.gateway.atmategateway.database.services;

import com.atmate.portal.gateway.atmategateway.database.entitites.ClientNotification;
import com.atmate.portal.gateway.atmategateway.database.repos.ClientNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientNotificationService {

    private final ClientNotificationRepository clientNotificationRepository;

    @Autowired
    public ClientNotificationService(ClientNotificationRepository clientNotificationRepository) {
        this.clientNotificationRepository = clientNotificationRepository;
    }

    // Criar uma nova notificação do cliente
    public ClientNotification createClientNotification(ClientNotification clientNotification) {
        return clientNotificationRepository.save(clientNotification);
    }

    // Ler todas as notificações do cliente
    public List<ClientNotification> getAllClientNotifications() {
        return clientNotificationRepository.findAll();
    }

    // Ler uma notificação do cliente por ID
    public Optional<ClientNotification> getClientNotificationById(Integer id) {
        return clientNotificationRepository.findById(id);
    }

    // Atualizar uma notificação do cliente
    public ClientNotification updateClientNotification(Integer id, ClientNotification clientNotificationDetails) {
        ClientNotification clientNotification = clientNotificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificação do cliente não encontrada com ID: " + id));

        clientNotification = clientNotificationDetails;

        return clientNotificationRepository.save(clientNotification);
    }

    // Deletar uma notificação do cliente
    public void deleteClientNotification(Integer id) {
        if (!clientNotificationRepository.existsById(id)) {
            throw new RuntimeException("Notificação do cliente não encontrada com ID: " + id);
        }
        clientNotificationRepository.deleteById(id);
    }
}
