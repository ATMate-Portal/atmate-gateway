package com.atmate.portal.gateway.atmategateway.database.services;

import com.atmate.portal.gateway.atmategateway.database.dto.*;
import com.atmate.portal.gateway.atmategateway.database.entitites.Address;
import com.atmate.portal.gateway.atmategateway.database.entitites.Client;
import com.atmate.portal.gateway.atmategateway.database.entitites.Contact;
import com.atmate.portal.gateway.atmategateway.database.entitites.Tax;
import com.atmate.portal.gateway.atmategateway.database.repos.ClientRepository;
import com.atmate.portal.gateway.atmategateway.utils.enums.ErrorEnum;
import com.atmate.portal.gateway.atmategateway.utils.exceptions.ATMateException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ContactService contactService;
    @Autowired
    private TaxService taxService;
    @Autowired
    private ClientNotificationService clientNotificationService;

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

    public ClientInfoResponseDTO getClientDetails(Integer id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ATMateException(ErrorEnum.CLIENT_NOT_FOUND));

        ClientInfoResponseDTO clientDetails = new ClientInfoResponseDTO();
        clientDetails.setId(client.getId());
        clientDetails.setName(client.getName());
        clientDetails.setNif(client.getNif());
        clientDetails.setGender(client.getGender());
        clientDetails.setNationality(client.getNationality());
        clientDetails.setAssociatedColaborator(client.getAssociatedColaborator());
        clientDetails.setBirthDate(client.getBirthDate());
        clientDetails.setLastRefreshDate(client.getLastRefreshDate());

        List<Address> clientAddressess = addressService.getAddressByClient(client);

        List<AddressDTO> addresses = clientAddressess.stream().map(address -> {
            AddressDTO dto = new AddressDTO();
            dto.setStreet(address.getStreet());
            dto.setDoorNumber(address.getDoorNumber());
            dto.setZipCode(address.getZipCode());
            dto.setCity(address.getCity());
            dto.setCounty(address.getCounty());
            dto.setDistrict(address.getDistrict());
            dto.setParish(address.getParish());
            dto.setCountry(address.getCountry());
            dto.setAddressTypeName(address.getAddressType().getDescription()); // Assumindo que AddressType tem "name"
            dto.setCreatedAt(address.getCreatedAt());
            dto.setUpdatedAt(address.getUpdatedAt());
            return dto;
        }).toList();

        List<Contact> clientContacts = contactService.getContactsByClient(client);

        List<ContactDTO> contacts = clientContacts.stream().map(contact -> {
            ContactDTO dto = new ContactDTO();
            dto.setContactTypeName(contact.getContactType().getDescription()); // Assumindo que ContactType tem "name"
            dto.setContact(contact.getContact());
            dto.setIsDefaultContact(contact.getIsDefaultContact());
            dto.setDescription(contact.getDescription());
            dto.setCreatedAt(contact.getCreatedAt());
            dto.setUpdatedAt(contact.getUpdatedAt());
            return dto;
        }).toList();

        List<Tax> clientTaxes = taxService.getTaxesByClient(client);

        List<TaxResponseDTO> taxes = clientTaxes.stream().map(tax -> {
            JsonNode jsonNode = null;
            try {
                TaxResponseDTO dto = new TaxResponseDTO();
                jsonNode = objectMapper.readTree(tax.getTaxData());

                String identifier = tax.getIdentifier(jsonNode);
                String amount = tax.getAmount(jsonNode);
                String state = tax.getState(jsonNode);

                if (identifier == null || amount == null || state == null) {
                    throw new ATMateException(ErrorEnum.INVALID_JSON_STRUCTURE);
                }

                TaxResponseDTO taxResponse = new TaxResponseDTO();
                taxResponse.setIdentificadorUnico(identifier);
                taxResponse.setTipo(tax.getTaxType().getDescription());
                taxResponse.setDataLimite(tax.getPaymentDeadline());

                taxResponse.setValor(amount.trim());
                taxResponse.setEstado(state);
                taxResponse.setClientName(tax.getClient().getName());
                taxResponse.setJson(tax.getTaxData());

                return taxResponse;
            } catch (JsonProcessingException e) {
                throw new ATMateException(ErrorEnum.INVALID_JSON);
            }
            
        }).toList();

        List<NotificationClientDTO> notifications = clientNotificationService.getClientNotificationByClientId(client);

        clientDetails.setAddresses(addresses);
        clientDetails.setContacts(contacts);
        clientDetails.setTaxes(taxes);
        clientDetails.setNotifications(notifications);

        return clientDetails;
    }


}
