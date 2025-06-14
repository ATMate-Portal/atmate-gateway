package com.atmate.portal.gateway.atmategateway.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

        @NotBlank(message = "O nome de utilizador é obrigatório")
        @Size(min = 3, max = 50, message = "O nome de utilizador deve ter entre 3 e 50 caracteres")
        private String username;

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "O email deve ser válido")
        private String email;

        @NotBlank(message = "A password é obrigatória")
        @Size(min = 8, message = "A password deve ter pelo menos 8 caracteres")
        private String password;
}