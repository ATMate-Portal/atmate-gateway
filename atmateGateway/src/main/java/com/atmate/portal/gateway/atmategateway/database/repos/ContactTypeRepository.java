package com.atmate.portal.gateway.atmategateway.database.repos;

import com.atmate.portal.gateway.atmategateway.database.entitites.ContactType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactTypeRepository extends JpaRepository<ContactType, Integer> {
    // Você pode adicionar métodos personalizados aqui, se necessário
}

