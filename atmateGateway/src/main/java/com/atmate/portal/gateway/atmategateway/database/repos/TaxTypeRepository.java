package com.atmate.portal.gateway.atmategateway.database.repos;

import com.atmate.portal.gateway.atmategateway.database.entitites.TaxType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxTypeRepository extends JpaRepository<TaxType, Integer> {
    // Você pode adicionar métodos personalizados aqui, se necessário
}
