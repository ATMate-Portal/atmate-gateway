package com.atmate.portal.gateway.atmategateway.database.services;

import com.atmate.portal.gateway.atmategateway.database.entitites.Configuration;
import com.atmate.portal.gateway.atmategateway.database.repos.ConfigurationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConfigurationService {

    @Autowired
    private ConfigurationRepository configurationRepository;

    public List<Configuration> getActiveConfigurations() {
        return configurationRepository.findByIsActiveTrue();
    }

    public Optional<Configuration> getConfigurationString (String varName) {
        return configurationRepository.findConfigurationByVarname(varName);
    }

    @Transactional
    public void saveConfigurations(List<Configuration> configurations) {
        for (Configuration config : configurations) {
            Configuration existing = configurationRepository.findConfigurationByVarname(config.getVarname())
                    .orElse(new Configuration());
            existing.setVarname(config.getVarname());
            existing.setVarvalue(config.getVarvalue());
            existing.setDescription(config.getDescription());
            existing.setIsActive(true);
            configurationRepository.save(existing);
        }
    }
}
