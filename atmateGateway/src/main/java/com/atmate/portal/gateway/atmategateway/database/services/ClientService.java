package com.atmate.portal.gateway.atmategateway.database.services;

import com.atmate.portal.gateway.atmategateway.database.dto.ClientFilterDTO;
import com.atmate.portal.gateway.atmategateway.database.dto.ClientResponseDTO;
import com.atmate.portal.gateway.atmategateway.database.entitites.Client;
import com.atmate.portal.gateway.atmategateway.database.repos.ClientRepository;
import com.atmate.portal.gateway.atmategateway.database.specification.ClientSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
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

    public Page<ClientResponseDTO> getClients(ClientFilterDTO filter, Pageable pageable) {
        return clientRepository.findAll(ClientSpecification.withFilters(filter), pageable)
                .map(this::toDTO);
    }

    private ClientResponseDTO toDTO(Client client) {
        ClientResponseDTO dto = new ClientResponseDTO();
        dto.setId(client.getId());
        dto.setName(client.getName());
        dto.setNif(client.getNif());
        dto.setGender(client.getGender());
        dto.setNationality(client.getNationality());
        dto.setAssociatedColaborator(client.getAssociatedColaborator());
        dto.setBirthDate(client.getBirthDate());
        dto.setLastRefreshDate(client.getLastRefreshDate());
        return dto;
    }
}
