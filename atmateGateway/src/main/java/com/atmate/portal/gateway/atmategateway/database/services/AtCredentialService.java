package com.atmate.portal.gateway.atmategateway.database.services;

import com.atmate.portal.gateway.atmategateway.database.entitites.AtCredential;
import com.atmate.portal.gateway.atmategateway.database.repos.AtCredentialRepository;
import com.atmate.portal.gateway.atmategateway.services.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AtCredentialService {

    private final AtCredentialRepository atCredentialRepository;
    @Autowired
    private CryptoService cryptoService;


    @Autowired
    public AtCredentialService(AtCredentialRepository atCredentialRepository) {
        this.atCredentialRepository = atCredentialRepository;
    }

    // Criar uma nova credencial
    public AtCredential createAtCredential(AtCredential atCredential) throws Exception {

        String unmaskedPassword = atCredential.getPassword(); //12345

        atCredential.setPassword(cryptoService.encrypt(unmaskedPassword));

        return atCredentialRepository.save(atCredential);
    }

    // Ler todas as credenciais
    public List<AtCredential> getAllAtCredentials() {
        return atCredentialRepository.findAll();
    }

    // Ler uma credencial por ID
    public Optional<AtCredential> getAtCredentialById(Integer id) {
        return atCredentialRepository.findById(id);
    }

    // Atualizar uma credencial
    public AtCredential updateAtCredential(Integer id, AtCredential atCredentialDetails) {
        AtCredential atCredential = atCredentialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Credencial não encontrada com ID: " + id));

        atCredential = atCredentialDetails;

        return atCredentialRepository.save(atCredential);
    }

    // Deletar uma credencial
    public void deleteATCredentialByClientId(Integer id) {
        if (!atCredentialRepository.existsAtCredentialsByClientId(id)) {
            throw new RuntimeException("Endereço não encontrado com ID: " + id);
        }

        atCredentialRepository.deleteAtCredentialByClientId(id);
    }
}
