package com.atmate.portal.gateway.atmategateway.database.services;

import com.atmate.portal.gateway.atmategateway.database.dto.ClientResponseDTO;
import com.atmate.portal.gateway.atmategateway.database.entitites.Client;
import com.atmate.portal.gateway.atmategateway.database.repos.ClientRepository;
import com.atmate.portal.gateway.atmategateway.utils.enums.ErrorEnum;
import com.atmate.portal.gateway.atmategateway.utils.exceptions.ATMateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // Criar um novo cliente
    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    // Ler todos os clientes
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    // Ler um cliente por ID
    public Optional<Client> getClientById(Integer id) {
        return clientRepository.findById(id);
    }

    public boolean existsByNif(Integer nif) {
        return clientRepository.existsByNif(nif);
    }

    // Atualizar um cliente
    public Client updateClient(Integer id, Client clientDetails) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + id));

        client = clientDetails;

        return clientRepository.save(client);
    }

    // Deletar um cliente
    public void deleteClient(Integer id) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado com ID: " + id);
        }

        clientRepository.deleteById(id);
    }

    public List<ClientResponseDTO> getClients() {
        List<ClientResponseDTO> clientList = new ArrayList<>();

        List<Client> clients = clientRepository.findAll();
        
        if (clients.isEmpty()) {
            log.warn("Não foram encontrados clientes. Necessário consultar BD");
            throw new ATMateException(ErrorEnum.CLIENT_NOT_FOUND);
        }

        for (Client client : clients) {
            ClientResponseDTO dto = new ClientResponseDTO(
                    client.getId(),
                    client.getName(),
                    client.getNif(),
                    client.getGender(),
                    client.getNationality(),
                    client.getAssociatedColaborator(),
                    client.getBirthDate(),
                    client.getLastRefreshDate());

            log.info("Cliente: " + client.getName() + " com o NIF: " + client.getNif() + " a ser retornado da BD");

            clientList.add(dto);
        }

        log.info("Número de clientes retornados: " + clients.size());

        return clientList;
    }

}
