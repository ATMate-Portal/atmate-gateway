package com.atmate.portal.gateway.atmategateway.database.repos;

import com.atmate.portal.gateway.atmategateway.database.entitites.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {
    // Você pode adicionar métodos personalizados aqui, se necessário

    void deleteContactByClientId(int id);

    boolean existsContactByClientId(int id);

}

