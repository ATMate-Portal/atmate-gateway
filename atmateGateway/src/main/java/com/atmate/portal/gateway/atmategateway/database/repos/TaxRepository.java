package com.atmate.portal.gateway.atmategateway.database.repos;

import com.atmate.portal.gateway.atmategateway.database.entitites.Tax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxRepository extends JpaRepository<Tax, Integer> {
    // Você pode adicionar métodos personalizados aqui, se necessário
}

