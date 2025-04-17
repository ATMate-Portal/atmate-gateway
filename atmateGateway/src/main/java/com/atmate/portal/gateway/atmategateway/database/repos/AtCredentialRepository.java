package com.atmate.portal.gateway.atmategateway.database.repos;


import com.atmate.portal.gateway.atmategateway.database.entitites.AtCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtCredentialRepository extends JpaRepository<AtCredential, Integer> {
    // Você pode adicionar métodos personalizados aqui, se necessário

    boolean deleteAtCredentialByClientId(int id);

    boolean existsAtCredentialsByClientId(int id);

}
