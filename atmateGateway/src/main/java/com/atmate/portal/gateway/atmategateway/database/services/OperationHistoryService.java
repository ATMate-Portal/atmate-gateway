package com.atmate.portal.gateway.atmategateway.database.services;

import com.atmate.portal.gateway.atmategateway.database.dto.OperationHistoryDTO;
import com.atmate.portal.gateway.atmategateway.database.dto.OperationHistoryRequestDTO;
import com.atmate.portal.gateway.atmategateway.database.entitites.User;
import com.atmate.portal.gateway.atmategateway.utils.enums.OperationHistoryActionsEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import com.atmate.portal.gateway.atmategateway.database.entitites.OperationHistory;
import com.atmate.portal.gateway.atmategateway.database.repos.OperationHistoryRepository;
import com.atmate.portal.gateway.atmategateway.database.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class OperationHistoryService {

    @Autowired
    OperationHistoryRepository operationHistoryRepository;
    @Autowired
    UserRepository userRepository;

    public Page<OperationHistoryDTO> getOperationHistory(Integer userId, String actionCode, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return operationHistoryRepository.findWithFilters(userId, actionCode, startDate, endDate, pageable)
                .map(operation -> {
                    // Buscar nome do usuário (se disponível)
                    String userName;
                    userName = userRepository.findById(operation.getUser().getId())
                                .map(user -> user.getUsername() != null ? user.getUsername() : "Utilizador " + operation.getUser().getId())
                                .orElse("Utilizador Desconhecido");

                    // Formatar mensagem com base no actionCode
                    String formattedMessage = OperationHistoryActionsEnum.fromActionCode(operation.getUserAction())
                            .map(action -> {
                                // Simulação de parâmetro de contexto (substitua pela lógica real)
                                String contextParam = getContextParameter(action, operation);
                                return action.formatMessage(userName, contextParam);
                            })
                            .orElse("Ação desconhecida: " + operation.getUserAction());

                    return new OperationHistoryDTO(
                            operation.getId(),
                            operation.getUser().getId(),
                            operation.getUser().getUsername(),
                            formattedMessage,
                            operation.getCreatedAt()
                    );
                });
    }

    public OperationHistoryDTO createOperationHistory(OperationHistoryRequestDTO request) {
        // Validar actionCode
        OperationHistoryActionsEnum action = OperationHistoryActionsEnum.fromActionCode(request.getActionCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid action code: " + request.getActionCode()));

        // Determinar userId (default to 1 if null)
        Integer userId = request.getUserId() != null ? request.getUserId() : 1;

        // Buscar usuário e nome do usuário
        Optional<User> user = userRepository.findById(userId);
        String userName = user
                .map(u -> u.getUsername() != null ? u.getUsername() : "Utilizador " + userId)
                .orElse("Utilizador Desconhecido");


        // Criar entidade
        OperationHistory operation = new OperationHistory();
        operation.setUser(user.orElse(null));
        operation.setUserAction(action.getMessage());
        operation.setCreatedAt(LocalDateTime.now());

        // Salvar no banco
        OperationHistory savedOperation = operationHistoryRepository.save(operation);

        // Formatar mensagem
        String contextParam = request.getContextParameter() != null ? request.getContextParameter() : getContextParameter(action, savedOperation);
        String formattedMessage = action.formatMessage(userName, contextParam);

        // Retornar DTO
        return new OperationHistoryDTO(
                savedOperation.getId(),
                savedOperation.getUser().getId(),
                operation.getUser().getUsername(),
                formattedMessage,
                savedOperation.getCreatedAt()
        );
    }

    // Método para obter o parâmetro de contexto (simulado, ajuste conforme sua lógica)
    private String getContextParameter(OperationHistoryActionsEnum action, OperationHistory operation) {
        // Exemplo: número de registros ou nome do cliente
        switch (action) {
            case CHECK_URGENT_FISCAL_TAX:
            case CHECK_ALL_FISCAL_TAX:
            case CHECK_ALL_CLIENTS:
                return "10"; // Simulado, substitua por contagem real
            case ADD_CLIENT:
            case DELETE_CLIENT:
                return "Cliente Exemplo"; // Substitua por nome/ID real do cliente
            case CHANGE_CONFIG:
                return "prazos fiscais"; // Substitua por parâmetro real
            default:
                return "Desconhecido";
        }
    }
}
