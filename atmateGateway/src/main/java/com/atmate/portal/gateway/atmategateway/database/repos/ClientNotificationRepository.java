package com.atmate.portal.gateway.atmategateway.database.repos;


import com.atmate.portal.gateway.atmategateway.database.entitites.ClientNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientNotificationRepository extends JpaRepository<ClientNotification, Integer> {
    // Você pode adicionar métodos personalizados aqui, se necessário

    boolean existsClientNotificationByClientId(Integer id);

    void deleteClientNotificationByClientId(Integer id);
}

