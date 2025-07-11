package com.atmate.portal.gateway.atmategateway.database.repos;


import com.atmate.portal.gateway.atmategateway.database.entitites.Client;
import com.atmate.portal.gateway.atmategateway.database.entitites.ClientNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientNotificationRepository extends JpaRepository<ClientNotification, Integer> {
    // Você pode adicionar métodos personalizados aqui, se necessário

    boolean existsClientNotificationByClientId(Integer id);

    void deleteClientNotificationByClientId(Integer id);

    void deleteAllByClientNotificationConfigId(Integer id);

    boolean existsClientNotificationByClientNotificationConfigId(Integer id);

    List<ClientNotification> findAllByClientNotificationConfigIdIn(List<Integer> ids);

    List<ClientNotification> getClientNotificationByClient(Client client);
}

