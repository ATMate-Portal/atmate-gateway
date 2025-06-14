package com.atmate.portal.gateway.atmategateway.database.services;

import com.atmate.portal.gateway.atmategateway.dto.OperationHistoryResponse;
import com.atmate.portal.gateway.atmategateway.dto.OperationHistoryRequest;
import com.atmate.portal.gateway.atmategateway.database.entitites.User;
import com.atmate.portal.gateway.atmategateway.utils.enums.OperationHistoryActionsEnum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication; // <<< IMPORTAR Authentication
import org.springframework.security.core.context.SecurityContextHolder; // <<< IMPORTAR SecurityContextHolder
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.atmate.portal.gateway.atmategateway.database.entitites.OperationHistory;
import com.atmate.portal.gateway.atmategateway.database.repos.OperationHistoryRepository;
import com.atmate.portal.gateway.atmategateway.database.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional; // Boa prática para métodos de escrita

@Slf4j
@Service
public class OperationHistoryService {

    private final OperationHistoryRepository operationHistoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public OperationHistoryService(OperationHistoryRepository operationHistoryRepository, UserRepository userRepository) {
        this.operationHistoryRepository = operationHistoryRepository;
        this.userRepository = userRepository;
    }

    // Método auxiliar para obter o ID do utilizador logado
    private Integer getCurrentAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
            String userEmail; // Ou username, dependendo do que o token e o UserDetails contêm como identificador principal

            Object principal = authentication.getPrincipal();

            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                userEmail = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                // Se o principal for apenas uma String (menos comum com UsernamePasswordAuthenticationToken
                // usando UserDetails, mas como fallback)
                userEmail = (String) principal;
            } else {
                // Se o principal não for UserDetails nem String, não sabemos como obter o identificador
                log.warn("Tipo de principal inesperado: {}", principal.getClass().getName());
                return 1;
            }

            if (userEmail != null) {
                // Usa o userRepository (que já tens injetado no serviço) para encontrar o User pelo email
                Optional<User> userOptional = userRepository.findByEmail(userEmail);
                if (userOptional.isPresent()) {
                    return userOptional.get().getId(); // Retorna o ID Long da tua entidade User
                } else {
                    // Isto seria estranho se o token é válido mas o email do token não existe na BD
                    log.error("Utilizador autenticado com email '{}' não encontrado na base de dados.", userEmail);
                    return 1;
                }
            }
        }

        // Se chegou aqui, não há autenticação válida ou não foi possível obter o email.
        log.warn("Nenhum utilizador autenticado válido encontrado para registar a operação.");
        return 1;
    }

    public Page<OperationHistoryResponse> getOperationHistory(
            Integer userId, // Este userId pode ser para filtrar o histórico de um user específico por um admin, por exemplo
            String actionCode,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {
        return operationHistoryRepository.findWithFilters(userId, actionCode, startDate, endDate, pageable)
                .map(operation -> new OperationHistoryResponse(
                        operation.getId(),
                        operation.getUser() != null ? operation.getUser().getId() : null, // Lidar com user null
                        operation.getUser() != null && operation.getUser().getUsername() != null
                                ? operation.getUser().getUsername()
                                : "Utilizador Desconhecido",
                        operation.getUserAction(),
                        operation.getCreatedAt()
                ));
    }

    @Transactional // Adicionar @Transactional para operações de escrita
    public OperationHistoryResponse createOperationHistory(OperationHistoryRequest request) {
        // Validar actionCode
        OperationHistoryActionsEnum action = OperationHistoryActionsEnum.fromActionCode(request.getActionCode())
                .orElseThrow(() -> new IllegalArgumentException("Código de ação inválido: " + request.getActionCode()));

        // Obter o ID do utilizador autenticado
        int authenticatedUserId = getCurrentAuthenticatedUserId();

        User authenticatedUser = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new IllegalStateException("Utilizador autenticado com ID " + authenticatedUserId + " não encontrado na base de dados."));

        String userName = authenticatedUser.getUsername() != null ? authenticatedUser.getUsername() : "Utilizador Desconhecido";

        // Criar entidade
        OperationHistory operation = new OperationHistory();
        operation.setUser(authenticatedUser); // Associar a operação ao utilizador autenticado
        operation.setCreatedAt(LocalDateTime.now());

        // Formatar a mensagem da ação
        // O request.getContextParameter() ainda pode ser útil para detalhes específicos da ação
        String formattedMessage = action.formatMessage(userName, request.getContextParameter());
        operation.setUserAction(formattedMessage);

        // Salvar no banco
        OperationHistory savedOperation = operationHistoryRepository.save(operation);

        // Retornar DTO
        return new OperationHistoryResponse(
                savedOperation.getId(),
                savedOperation.getUser() != null ? savedOperation.getUser().getId() : null,
                userName, // Usar o userName obtido do utilizador autenticado
                formattedMessage,
                savedOperation.getCreatedAt()
        );
    }
}