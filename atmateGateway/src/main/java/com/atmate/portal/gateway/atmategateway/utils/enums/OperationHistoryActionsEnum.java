package com.atmate.portal.gateway.atmategateway.utils.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.text.MessageFormat; // <-- Importar MessageFormat
import java.util.Arrays;
import java.util.Optional;

/**
 * Enum representing possible actions recorded in the operation history.
 * Each action has a unique code and a message template with placeholders:
 * {0} for the user name or ID, {1} for additional context (e.g., number of records, parameter changed).
 */
@Getter
@RequiredArgsConstructor
public enum OperationHistoryActionsEnum {

    /**
     * Consultation of urgent fiscal obligations.
     * {0}: User name or ID, {1}: Number of obligations found.
     */
    CHECK_URGENT_FISCAL_TAX("CHECK-001", "Utilizador {0} consultou {1} obrigações fiscais urgentes."),

    /**
     * Consultation of all fiscal obligations.
     * {0}: User name or ID, {1}: Number of obligations found.
     */
    CHECK_ALL_FISCAL_TAX("CHECK-002", "Utilizador {0} consultou {1} obrigações fiscais."),

    /**
     * Consultation of all clients.
     * {0}: User name or ID, {1}: Number of clients found.
     */
    CHECK_ALL_CLIENTS("CHECK-003", "Utilizador {0} consultou {1} clientes."),

    /**
     * Consultation of operation history.
     * {0}: User name or ID, {1}: Number of operations found.
     */
    CHECK_HISTORIC("CHECK-004", "Utilizador {0} consultou {1} operações no histórico."),

    CHECK_CONFIG("CHECK-005", "Utilizador {0} consultou a parametrização."),

    CHECK_NOTIFICATION_CONFIG("CHECK-006", "Utilizador {0} consultou {1} configurações de notificações."),

    CHECK_NOTIFICATIONS("CHECK-007", "Utilizador {0} consultou {1} notificações."),
    /**
     * Creation of a new client.
     * {0}: User name or ID, {1}: Client name or ID.
     */
    ADD_CLIENT("ADD-001", "Utilizador {0} criou o cliente {1}."),
    /**
     * Creation of a new client.
     * {0}: User name or ID, {1}: Client name or ID.
     */
    ADD_NOTIFICATION_CONFIG("ADD-002", "Utilizador {0} criou {1} configurações."),
    /**
     * Deletion of a client.
     * {0}: User name or ID, {1}: Client name or ID.
     */
    DELETE_CLIENT("DEL-001", "Utilizador {0} eliminou o cliente {1}."),
    DELETE_NOTIFICATION_CONFIG("DEL-002", "Utilizador {0} eliminou a configuração de notificação {1}."),

    UPDATE_CONFIG_NOTIFICATION_STATUS("UPD-001", "Utilizador {0} atualizou o estado da notificação {1}."),

    UPDATE_CONFIG_NOTIFICATION("UPD-002", "Utilizador {0} atualizou a notificação {1}."),

    FORCE_SEND_NOTIFICATION("FORCE-SEND-001", "Utilizador {0} enviou {1} notificações."),

    /**
     * Modification of configuration deadlines.
     * {0}: User name or ID, {1}: Parameter changed (e.g., deadline type).
     */
    CHANGE_CONFIG("CONF-001", "Utilizador {0} alterou a parametrização de {1}.");


    private final String actionCode;
    private final String message;

    /**
     * Retrieves an OperationHistoryActionsEnum by its action code.
     *
     * @param actionCode The action code to search for.
     * @return An Optional containing the matching enum value, or empty if no match is found.
     */
    public static Optional<OperationHistoryActionsEnum> fromActionCode(String actionCode) {
        return Arrays.stream(values())
                .filter(action -> action.getActionCode().equalsIgnoreCase(actionCode))
                .findFirst();
    }

    /**
     * Formats the message template with the provided parameters.
     * Uses MessageFormat to handle {0}, {1} placeholders.
     *
     * @param params The parameters to insert into the message template.
     * @return The formatted message.
     * @throws IllegalArgumentException if the format is invalid or arguments mismatch.
     */
    public String formatMessage(Object... params) {
        try {
            // Usar MessageFormat.format em vez de String.format
            return MessageFormat.format(this.message, params);
        } catch (IllegalArgumentException e) { // MessageFormat lança IllegalArgumentException
            // Considerar logar o erro ou lançar uma exceção mais específica da sua aplicação
            System.err.println("Erro ao formatar mensagem: " + this.message + " com params: " + Arrays.toString(params));
            // Lançar a exceção para indicar que a formatação falhou
            throw new IllegalArgumentException("Parâmetros inválidos ou formato incorreto para a mensagem: " + this.message, e);
        }
    }
}