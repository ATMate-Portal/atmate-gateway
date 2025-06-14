package com.atmate.portal.gateway.atmategateway.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class IntegrationAPIService {

    private final RestTemplate restTemplate;

    public IntegrationAPIService() {
        this.restTemplate = new RestTemplate();
    }

    public void syncClient(Integer clientId, boolean getTypeFromAT) {
        String url = "http://localhost:8080/atmate-integration/gateway/sync/" + clientId + "?getTypeFromAT=" + getTypeFromAT;

        ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Erro ao sincronizar cliente com ID " + clientId);
        }
    }

    public int sendNotification(Integer configId) {
        String url = "http://localhost:8080/atmate-integration/gateway/sendNotification/" + configId;
        log.info("A chamar o endpoint de envio: {}", url);

        try {
            // *** ALTERAÇÃO AQUI: Use Integer.class e ResponseEntity<Integer> ***
            ResponseEntity<Integer> response = restTemplate.getForEntity(url, Integer.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Integer body = response.getBody();
                // Verifica se o corpo não é nulo antes de retornar
                if (body != null) {
                    log.info("Notificações enviadas com sucesso. Quantidade: {}", body);
                    return body; // Retorna o Integer (será auto-unboxed para int)
                } else {
                    log.error("Endpoint retornou sucesso, mas o corpo da resposta está nulo para configId: {}", configId);
                    throw new RuntimeException("Resposta inesperada do servidor (corpo nulo) para configId " + configId);
                }
            } else {
                log.error("Erro ao chamar o endpoint. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Erro ao sincronizar cliente com ID " + configId + ". Status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Exceção ao chamar o endpoint sendNotification para configId: {}", configId, e);
            throw new RuntimeException("Falha na comunicação ao tentar enviar notificação para configId " + configId, e);
        }
    }
}
