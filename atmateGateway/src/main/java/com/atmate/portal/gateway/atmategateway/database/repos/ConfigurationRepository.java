package com.atmate.portal.gateway.atmategateway.database.repos;

import com.atmate.portal.gateway.atmategateway.database.entitites.Configuration;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Integer> {
    List<Configuration> findByIsActiveTrue();

    Optional<Configuration> findConfigurationByVarname(@NotBlank(message = "O nome da configuração é obrigatório") @Size(max = 50, message = "O nome da configuração deve ter no máximo 50 caracteres") String varname);

}