package com.atmate.portal.gateway.atmategateway.database.repos;

import com.atmate.portal.gateway.atmategateway.database.entitites.Client;
import com.atmate.portal.gateway.atmategateway.database.entitites.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {
    // Você pode adicionar métodos personalizados aqui, se necessário

    void deleteContactByClientId(int id);

    boolean existsContactByClientId(int id);

    List<Contact> findContactByClient(Client client);
}

