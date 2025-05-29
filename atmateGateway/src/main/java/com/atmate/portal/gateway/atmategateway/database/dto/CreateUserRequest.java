package com.atmate.portal.gateway.atmategateway.database.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Usando record para concisão (Java 16+)
// Se estiveres a usar uma versão anterior do Java, cria uma classe normal com getters e anotações.
public record CreateUserRequest(
        @NotBlank(message = "O nome de utilizador é obrigatório")
        @Size(min = 3, max = 50, message = "O nome de utilizador deve ter entre 3 e 50 caracteres")
        String username,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "O email deve ser válido")
        String email,

        @NotBlank(message = "A password é obrigatória")
        @Size(min = 8, message = "A password deve ter pelo menos 8 caracteres")
        String password
        // Podes adicionar mais campos como 'name' se for diferente de 'username'
) {
}
