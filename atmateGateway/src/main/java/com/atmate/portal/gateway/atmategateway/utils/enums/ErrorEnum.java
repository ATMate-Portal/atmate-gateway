package com.atmate.portal.gateway.atmategateway.utils.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorEnum {

    // Erros comuns (exemplo)
    TAX_NOT_FOUND("DATA-001", "Não foi encontrado nenhum imposto"),
    DATABASE_ERROR("DATA-002", "Erro ao aceder a base de dados"),
    INVALID_JSON("DATA-003", "Erro ao processar dados do imposto. Estrutura inválida."),
    INVALID_JSON_STRUCTURE("DATA-004", "Erro ao processar dados do imposto. Valores vazios."),
    INVALID_TAX_DATA("DATA-005", "Erro ao processar dados do imposto. Contém campos inválidos."),
    INVALID_TAX_DEADLINE_DATE("DATA-006", "Erro ao processar dados do imposto. Data Limite Pagamento inválida."),
    INVALID_TAX_CLIENT("DATA-007", "Erro ao processar dados do imposto. O cliente associado é inválido."),

    // Erros de autenticação (exemplo)
    AUTHENTICATION_FAILED("AUTH001", "Falha na autenticação."),
    UNAUTHORIZED("AUTH002", "Não autorizado."),


    // Adicione mais códigos de erro conforme necessário
    GENERIC_ERROR("GENERIC-001", "Erro Interno no Servidor");
    private final String errorCode;
    private final String message;
}