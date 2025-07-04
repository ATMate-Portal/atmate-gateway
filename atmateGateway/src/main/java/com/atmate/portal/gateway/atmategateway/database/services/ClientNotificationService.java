package com.atmate.portal.gateway.atmategateway.database.services;

import com.atmate.portal.gateway.atmategateway.dto.NotificationClientResponse;
import com.atmate.portal.gateway.atmategateway.database.entitites.Client;
import com.atmate.portal.gateway.atmategateway.database.entitites.ClientNotification;
import com.atmate.portal.gateway.atmategateway.database.repos.ClientNotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClientNotificationService {

    @Autowired
    private ClientNotificationRepository clientNotificationRepository;

    // Criar uma nova notificação do cliente
    public ClientNotification createClientNotification(ClientNotification clientNotification) {
        return clientNotificationRepository.save(clientNotification);
    }

    // Ler todas as notificações do cliente
    public List<ClientNotification> getAllClientNotifications() {
        return clientNotificationRepository.findAll();
    }

    public boolean existsClientNotificationsByConfigurationId(Integer id) {
        return clientNotificationRepository.existsClientNotificationByClientNotificationConfigId(id);
    }

    // Ler uma notificação do cliente por ID
    public Optional<ClientNotification> getClientNotificationById(Integer id) {
        return clientNotificationRepository.findById(id);
    }

    // Ler uma notificação do cliente por ID
    public List<NotificationClientResponse> getClientNotificationByClientId(Client client) {
        List<ClientNotification> list = clientNotificationRepository.getClientNotificationByClient(client);
        List<NotificationClientResponse> listOut = new ArrayList<>();
        for(ClientNotification cn : list){
            NotificationClientResponse cnDTO = new NotificationClientResponse();
            cnDTO.setClientId(cn.getClient().getName());
            cnDTO.setNotificationType(cn.getNotificationType().getDescription());
            cnDTO.setTaxType(cn.getTaxType().getDescription());
            cnDTO.setStatus(cn.getStatus());
            cnDTO.setTitle(cn.getTitle());
            cnDTO.setMessage(cn.getMessage());
            cnDTO.setSendDate(cn.getSendDate());

            listOut.add(cnDTO);
        }
        return listOut;
    }

    // Atualizar uma notificação do cliente
    public ClientNotification updateClientNotification(Integer id, ClientNotification clientNotificationDetails) {
        ClientNotification clientNotification = clientNotificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificação do cliente não encontrada com ID: " + id));

        clientNotification = clientNotificationDetails;

        return clientNotificationRepository.save(clientNotification);
    }

    public List<ClientNotification> getClientNotificationsByConfigsId(List<Integer> ids){
        return clientNotificationRepository.findAllByClientNotificationConfigIdIn(ids);
    }

    @Transactional
    public void deleteClientNotificationByClientId(Integer id) {
        if (!clientNotificationRepository.existsClientNotificationByClientId(id)) {
            System.out.println("Notificação do cliente não encontrada com ID: " + id);
        }
        clientNotificationRepository.deleteClientNotificationByClientId(id);
    }

    @Transactional
    public void deleteAllNotificationsByConfigurationId(Integer id){
        clientNotificationRepository.deleteAllByClientNotificationConfigId(id);
    }
}
