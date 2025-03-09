package com.atmate.portal.gateway.atmategateway.database.repos;

import com.atmate.portal.gateway.atmategateway.database.entitites.ClientType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientTypeRepository extends JpaRepository<ClientType, Integer> {
    // Você pode adicionar métodos personalizados aqui, se necessário
}

