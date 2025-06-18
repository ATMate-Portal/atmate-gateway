package com.atmate.portal.gateway.atmategateway.database.services;

import com.atmate.portal.gateway.atmategateway.database.entitites.*;
import com.atmate.portal.gateway.atmategateway.database.repos.ClientRepository;
import com.atmate.portal.gateway.atmategateway.dto.ClientResponse;
import com.atmate.portal.gateway.atmategateway.utils.enums.ErrorEnum;
import com.atmate.portal.gateway.atmategateway.utils.exceptions.ATMateException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Adicionado para lidar com UnnecessaryStubbingException
class ClientServiceTest {

    @Mock // Cria um mock (versão simulada) do ClientRepository
    private ClientRepository clientRepository;
    @Mock // Cria um mock do ObjectMapper
    private ObjectMapper objectMapper;
    @Mock // Cria um mock do AddressService
    private AddressService addressService;
    @Mock // Cria um mock do ContactService
    private ContactService contactService;
    @Mock // Cria um mock do TaxService
    private TaxService taxService;
    @Mock // Cria um mock do ClientNotificationService
    private ClientNotificationService clientNotificationService;

    @InjectMocks // Injeta os mocks criados acima na instância real do ClientService
    private ClientService clientService;

    // Declaração de objetos de teste que serão usados em vários testes
    private Client testClient;
    private Address testAddress;
    private Contact testContact;
    private Tax testTax; // Um mock de Tax para testes complexos
    private TaxType testTaxType;
    private AddressType testAddressType;
    private ContactType testContactType;

    @BeforeEach // Este método é executado antes de cada teste
    void setUp() {
        // Inicializar entidades e DTOs de teste com dados de exemplo
        testClient = new Client();
        testClient.setId(1);
        testClient.setName("Test Client");
        testClient.setNif(123456789); // NIF agora é Integer
        testClient.setGender("M");
        testClient.setNationality("Portuguesa");
        testClient.setAssociatedColaborator("Colab1");
        testClient.setBirthDate(LocalDate.of(1990, 1, 1));
        testClient.setLastRefreshDate(LocalDateTime.now());

        testAddressType = new AddressType();
        testAddressType.setDescription("Residencial");

        testAddress = new Address();
        testAddress.setClient(testClient);
        testAddress.setStreet("Rua Teste");
        testAddress.setDoorNumber("10");
        testAddress.setZipCode("1234-567");
        testAddress.setCity("Lisboa");
        testAddress.setCountry("Portugal");
        testAddress.setAddressType(testAddressType);
        testAddress.setCreatedAt(LocalDateTime.now());
        testAddress.setUpdatedAt(LocalDateTime.now());

        testContactType = new ContactType();
        testContactType.setDescription("Telefone");

        testContact = new Contact();
        testContact.setClient(testClient);
        testContact.setContactType(testContactType);
        testContact.setContact("912345678");
        testContact.setIsDefaultContact(true);
        testContact.setDescription("Contacto principal");
        testContact.setCreatedAt(LocalDateTime.now());
        testContact.setUpdatedAt(LocalDateTime.now());

        testTaxType = new TaxType();
        testTaxType.setDescription("IRS");

        testTax = mock(Tax.class); // Cria um mock da entidade Tax
        // Define o comportamento dos métodos do mock testTax
        when(testTax.getTaxType()).thenReturn(testTaxType);
        when(testTax.getClient()).thenReturn(testClient);
        when(testTax.getPaymentDeadline()).thenReturn(LocalDate.of(2025, 12, 31));
        when(testTax.getTaxData()).thenReturn("{\"identifier\":\"ID123\",\"amount\":\"100.00 EUR\",\"state\":\"Pendente\"}");
        // Define o comportamento dos métodos de parsing da entidade Tax, quando recebem um JsonNode
        when(testTax.getIdentifier(any(JsonNode.class))).thenReturn("ID123");
        when(testTax.getAmount(any(JsonNode.class))).thenReturn("100.00 EUR");
        when(testTax.getState(any(JsonNode.class))).thenReturn("Pendente");
    }

    // --- Testes para createClient ---
    @Test // Marca este método como um teste JUnit
    @DisplayName("Deve criar um cliente com sucesso") // Nome legível para o teste
    void createClient_Success() {
        // Define o comportamento do mock: quando save for chamado, retorna testClient
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // Chama o método do serviço que está a ser testado
        Client createdClient = clientService.createClient(new Client());

        // Verifica os resultados (assertivas)
        assertNotNull(createdClient); // Garante que o objeto não é nulo
        assertEquals(testClient.getId(), createdClient.getId()); // Compara IDs
        // Verifica se o método save do mock clientRepository foi chamado exatamente uma vez
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    // --- Testes para getAllClients ---
    @Test
    @DisplayName("Deve retornar todos os clientes")
    void getAllClients_ReturnsAllClients() {
        // Mock: quando findAll for chamado, retorna uma lista com testClient
        when(clientRepository.findAll()).thenReturn(List.of(testClient));

        List<Client> clients = clientService.getAllClients();

        // Asserções para verificar se a lista não está vazia e tem o cliente esperado
        assertFalse(clients.isEmpty());
        assertEquals(1, clients.size());
        assertEquals(testClient.getName(), clients.get(0).getName());
        verify(clientRepository, times(1)).findAll(); // Verifica a chamada ao findAll
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não há clientes")
    void getAllClients_ReturnsEmptyList() {
        // Mock: quando findAll for chamado, retorna uma lista vazia
        when(clientRepository.findAll()).thenReturn(Collections.emptyList());

        List<Client> clients = clientService.getAllClients();

        assertTrue(clients.isEmpty()); // Verifica se a lista está vazia
        verify(clientRepository, times(1)).findAll();
    }

    // --- Testes para getClientById ---
    @Test
    @DisplayName("Deve retornar um cliente por ID quando existe")
    void getClientById_ExistingId_ReturnsClient() {
        // Mock: quando findById for chamado com ID 1, retorna Optional com testClient
        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));

        Optional<Client> result = clientService.getClientById(1);

        assertTrue(result.isPresent()); // Verifica se o Optional contém um valor
        assertEquals(testClient.getId(), result.get().getId());
        verify(clientRepository, times(1)).findById(1); // Verifica a chamada ao findById
    }

    @Test
    @DisplayName("Deve retornar Optional.empty quando o cliente não é encontrado por ID")
    void getClientById_NonExistingId_ReturnsEmptyOptional() {
        // Mock: quando findById for chamado com qualquer inteiro, retorna Optional.empty
        when(clientRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<Client> result = clientService.getClientById(99);

        assertTrue(result.isEmpty()); // Verifica se o Optional está vazio
        verify(clientRepository, times(1)).findById(99);
    }

    // --- Testes para existsByNif ---
    @Test
    @DisplayName("Deve retornar true se o NIF existe")
    void existsByNif_NifExists_ReturnsTrue() {
        // Mock: quando existsByNif for chamado com qualquer inteiro, retorna true
        when(clientRepository.existsByNif(anyInt())).thenReturn(true);

        assertTrue(clientService.existsByNif(123456789)); // Chama o serviço com um NIF
        verify(clientRepository, times(1)).existsByNif(123456789); // Verifica a chamada com o NIF exato
    }

    @Test
    @DisplayName("Deve retornar false se o NIF não existe")
    void existsByNif_NifDoesNotExist_ReturnsFalse() {
        // Mock: quando existsByNif for chamado com qualquer inteiro, retorna false
        when(clientRepository.existsByNif(anyInt())).thenReturn(false);

        assertFalse(clientService.existsByNif(999999999));
        verify(clientRepository, times(1)).existsByNif(999999999);
    }

    // --- Testes para updateClient ---
    @Test
    @DisplayName("Deve atualizar um cliente com sucesso")
    void updateClient_Success() {
        Client updatedClientDetails = new Client();
        updatedClientDetails.setName("Updated Name");
        updatedClientDetails.setNif(987654321); // NIF agora é Integer

        // Mock: quando findById, retorna o cliente existente
        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));
        // Mock: quando save, captura o argumento e o retorna (simula o save no repositório)
        when(clientRepository.save(any(Client.class))).thenAnswer((Answer<Client>) invocation -> {
            Client clientArg = invocation.getArgument(0);
            clientArg.setId(testClient.getId()); // Mantém o ID do cliente original
            return clientArg;
        });

        Client result = clientService.updateClient(1, updatedClientDetails);

        assertNotNull(result);
        assertEquals(testClient.getId(), result.getId());
        assertEquals("Updated Name", result.getName());
        assertEquals(987654321, result.getNif());
        verify(clientRepository, times(1)).findById(1); // Verifica se findById foi chamado
        verify(clientRepository, times(1)).save(any(Client.class)); // Verifica se save foi chamado
    }

    @Test
    @DisplayName("Deve lançar RuntimeException ao tentar atualizar cliente não encontrado")
    void updateClient_ClientNotFound_ThrowsException() {
        // Mock: quando findById, retorna Optional.empty
        when(clientRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Verifica se uma exceção específica é lançada
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            clientService.updateClient(99, new Client());
        });

        assertEquals("Cliente não encontrado com ID: 99", thrown.getMessage());
        verify(clientRepository, times(1)).findById(99);
        verify(clientRepository, never()).save(any(Client.class)); // Garante que save nunca foi chamado
    }

    // --- Testes para deleteClient ---
    @Test
    @DisplayName("Deve deletar um cliente com sucesso")
    void deleteClient_Success() {
        // Mock: quando existsById, retorna true (cliente existe)
        when(clientRepository.existsById(1)).thenReturn(true);
        // Mock: quando deleteById, não faz nada (para métodos void)
        doNothing().when(clientRepository).deleteById(1);

        clientService.deleteClient(1);

        verify(clientRepository, times(1)).existsById(1); // Verifica chamada de existsById
        verify(clientRepository, times(1)).deleteById(1); // Verifica chamada de deleteById
    }

    @Test
    @DisplayName("Deve lançar RuntimeException ao tentar deletar cliente não encontrado")
    void deleteClient_ClientNotFound_ThrowsException() {
        // Mock: quando existsById, retorna false (cliente não existe)
        when(clientRepository.existsById(anyInt())).thenReturn(false);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            clientService.deleteClient(99);
        });

        assertEquals("Cliente não encontrado com ID: 99", thrown.getMessage());
        verify(clientRepository, times(1)).existsById(99);
        verify(clientRepository, never()).deleteById(anyInt()); // Garante que deleteById nunca foi chamado
    }

    // --- Testes para getClients ---
    @Test
    @DisplayName("Deve retornar uma lista de ClientResponse com sucesso")
    void getClients_ReturnsClientResponseList() {
        // Mock: quando findAll, retorna uma lista com o cliente de teste
        when(clientRepository.findAll()).thenReturn(List.of(testClient));

        List<ClientResponse> clientResponses = clientService.getClients();

        assertFalse(clientResponses.isEmpty());
        assertEquals(1, clientResponses.size());
        assertEquals(testClient.getId(), clientResponses.get(0).getId());
        assertEquals(testClient.getNif(), clientResponses.get(0).getNif());
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve lançar ATMateException quando não há clientes em getClients")
    void getClients_NoClientsFound_ThrowsATMateException() {
        // Mock: quando findAll, retorna uma lista vazia
        when(clientRepository.findAll()).thenReturn(Collections.emptyList());

        ATMateException thrown = assertThrows(ATMateException.class, () -> {
            clientService.getClients();
        });

        assertEquals(ErrorEnum.CLIENT_NOT_FOUND, thrown.getErrorEnum());
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve lançar ATMateException quando cliente não é encontrado em getClientDetails")
    void getClientDetails_ClientNotFound_ThrowsATMateException() {
        // Mock: quando findById for chamado com qualquer inteiro, retorna Optional.empty
        when(clientRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Verifica se a exceção ATMateException é lançada
        ATMateException thrown = assertThrows(ATMateException.class, () -> {
            clientService.getClientDetails(99);
        });

        assertEquals(ErrorEnum.CLIENT_NOT_FOUND, thrown.getErrorEnum());
        verify(clientRepository, times(1)).findById(99);
        // Garante que nenhum outro serviço foi chamado (pois o cliente não foi encontrado)
        verifyNoInteractions(addressService, contactService, taxService, clientNotificationService);
    }

    @Test
    @DisplayName("Deve lançar ATMateException para JSON inválido em getClientDetails (Tax)")
    void getClientDetails_InvalidTaxJson_ThrowsATMateException() throws JsonProcessingException {
        // Configura mocks iniciais
        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));
        when(taxService.getTaxesByClient(testClient)).thenReturn(List.of(testTax));

        // Simula que o `taxData` é inválido e que o `ObjectMapper` lança `JsonProcessingException`
        when(testTax.getTaxData()).thenReturn("INVALID JSON");
        when(objectMapper.readTree("INVALID JSON")).thenThrow(new JsonProcessingException("Erro de parsing") {});

        // Verifica se a exceção ATMateException é lançada
        ATMateException thrown = assertThrows(ATMateException.class, () -> {
            clientService.getClientDetails(1);
        });

        assertEquals(ErrorEnum.INVALID_JSON, thrown.getErrorEnum());
        verify(clientRepository, times(1)).findById(1);
        verify(taxService, times(1)).getTaxesByClient(testClient);
        verify(objectMapper, times(1)).readTree("INVALID JSON"); // Verifica a chamada ao ObjectMapper
        // Garante que os métodos de parsing da entidade Tax não foram chamados (pois o JSON é inválido)
        verify(testTax, never()).getIdentifier(any(JsonNode.class));
        verify(testTax, never()).getAmount(any(JsonNode.class));
        verify(testTax, never()).getState(any(JsonNode.class));
    }

    @Test
    @DisplayName("Deve lançar ATMateException para estrutura JSON inválida em getClientDetails (Tax)")
    void getClientDetails_InvalidTaxJsonStructure_ThrowsATMateException() throws JsonProcessingException {
        // Configura mocks iniciais
        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));
        when(taxService.getTaxesByClient(testClient)).thenReturn(List.of(testTax));

        // Simula que um dos campos obrigatórios do JSON é nulo (getIdentifier retorna null)
        JsonNode mockJsonNode = mock(JsonNode.class);
        when(objectMapper.readTree(testTax.getTaxData())).thenReturn(mockJsonNode);
        when(testTax.getIdentifier(any(JsonNode.class))).thenReturn(null); // Simula campo 'identifier' nulo

        // Verifica se a exceção ATMateException é lançada
        ATMateException thrown = assertThrows(ATMateException.class, () -> {
            clientService.getClientDetails(1);
        });

        assertEquals(ErrorEnum.INVALID_JSON_STRUCTURE, thrown.getErrorEnum());
        verify(clientRepository, times(1)).findById(1);
        verify(taxService, times(1)).getTaxesByClient(testClient);
        verify(objectMapper, times(1)).readTree(testTax.getTaxData());
        // Verifica que `getIdentifier` foi chamado e retornou null (causando a exceção)
        verify(testTax, times(1)).getIdentifier(any(JsonNode.class));
        // Removidas as verificações 'never()' para getAmount/getState, pois a lógica do serviço as chama
        // mesmo que o 'identifier' seja nulo, devido ao uso do operador '||' na condição do if.
    }
}
