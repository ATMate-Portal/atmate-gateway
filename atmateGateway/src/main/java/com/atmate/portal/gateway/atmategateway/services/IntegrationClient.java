package com.atmate.portal.gateway.atmategateway.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IntegrationClient {

    private final RestTemplate restTemplate;

    public IntegrationClient() {
        this.restTemplate = new RestTemplate();
    }

    public void syncClient(Integer clientId, boolean getTypeFromAT) {
        String url = "http://localhost:8080/atmate-integration/clients/sync/" + clientId + "?getTypeFromAT=" + getTypeFromAT;

        ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Erro ao sincronizar cliente com ID " + clientId);
        }
    }
}
