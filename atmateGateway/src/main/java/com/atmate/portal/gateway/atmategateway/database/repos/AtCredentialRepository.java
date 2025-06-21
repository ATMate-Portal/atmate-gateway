package com.atmate.portal.gateway.atmategateway.database.repos;


import com.atmate.portal.gateway.atmategateway.database.entitites.AtCredential;
import com.atmate.portal.gateway.atmategateway.database.entitites.Client;
import com.atmate.portal.gateway.atmategateway.database.services.AtCredentialService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtCredentialRepository extends JpaRepository<AtCredential, Integer> {
    // Você pode adicionar métodos personalizados aqui, se necessário

    void deleteAtCredentialByClientId(int id);

    boolean existsAtCredentialsByClientId(int id);

    AtCredential getAtCredentialByClient(Client client);
}
