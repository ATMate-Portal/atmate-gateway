package com.atmate.portal.gateway.atmategateway.database.repos;

import com.atmate.portal.gateway.atmategateway.database.entitites.OperationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationHistoryRepository extends JpaRepository<OperationHistory, Integer> {
    // Você pode adicionar métodos personalizados aqui, se necessário
}

