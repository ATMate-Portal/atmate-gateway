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

    public Page<OperationHistoryDTO> getOperationHistory(
            Integer userId,
            String actionCode,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {
        return operationHistoryRepository.findWithFilters(userId, actionCode, startDate, endDate, pageable)
                .map(operation -> new OperationHistoryDTO(
                        operation.getId(),
                        operation.getUser().getId(),
                        operation.getUser().getUsername() != null
                                ? operation.getUser().getUsername()
                                : "Utilizador Desconhecido",
                        operation.getUserAction(), // Mensagem formatada diretamente do banco
                        operation.getCreatedAt()
                ));
    }

    public OperationHistoryDTO createOperationHistory(OperationHistoryRequestDTO request) {
        // Validar actionCode
        OperationHistoryActionsEnum action = OperationHistoryActionsEnum.fromActionCode(request.getActionCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid action code: " + request.getActionCode()));

        // Determinar userId (default to 1 if null)
        Integer userId = request.getUserId() != null ? request.getUserId() : 1;

        // Buscar usuário e nome do usuário
        Optional<User> user = userRepository.findById(userId);
        String userName = user.isPresent()?user.get().getUsername():"Utilizador Desconhecido";

        // Criar entidade
        OperationHistory operation = new OperationHistory();
        operation.setUser(user.orElse(null));

        operation.setCreatedAt(LocalDateTime.now());

        String formattedMessage = action.formatMessage(userName, request.getContextParameter());

        operation.setUserAction(formattedMessage);

        // Salvar no banco
        OperationHistory savedOperation = operationHistoryRepository.save(operation);

        // Retornar DTO
        return new OperationHistoryDTO(
                savedOperation.getId(),
                savedOperation.getUser().getId(),
                operation.getUser().getUsername(),
                formattedMessage,
                savedOperation.getCreatedAt()
        );
    }
}
