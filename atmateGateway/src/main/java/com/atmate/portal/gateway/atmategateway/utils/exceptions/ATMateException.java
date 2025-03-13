package com.atmate.portal.gateway.atmategateway.utils.exceptions;

import com.atmate.portal.gateway.atmategateway.utils.enums.ErrorEnum;
import lombok.Getter;

@Getter
public class ATMateException extends RuntimeException { // Estende RuntimeException para ser unchecked

    private final ErrorEnum errorEnum;

    public ATMateException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage()); // Passa a mensagem do ErrorEnum para a superclasse
        this.errorEnum = errorEnum;
    }

}