package com.atmate.portal.gateway.atmategateway.database.repos;

import com.atmate.portal.gateway.atmategateway.database.entitites.ClientNotificationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientNotificationConfigRepository extends JpaRepository<ClientNotificationConfig, Integer> {
    // Você pode adicionar métodos personalizados aqui, se necessário
}