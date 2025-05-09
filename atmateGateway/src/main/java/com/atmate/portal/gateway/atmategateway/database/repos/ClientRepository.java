package com.atmate.portal.gateway.atmategateway.database.repos;


import com.atmate.portal.gateway.atmategateway.database.entitites.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    boolean existsByNif(Integer nif);
}