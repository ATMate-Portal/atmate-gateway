package com.atmate.portal.gateway.atmategateway.controller;

import com.atmate.portal.gateway.atmategateway.dto.CreateUserRequest;
import com.atmate.portal.gateway.atmategateway.dto.LoginRequest;
import com.atmate.portal.gateway.atmategateway.dto.LoginResponse;
import com.atmate.portal.gateway.atmategateway.dto.UserDetailsResponse;
import com.atmate.portal.gateway.atmategateway.database.entitites.User;
import com.atmate.portal.gateway.atmategateway.database.services.UserService;
import com.atmate.portal.gateway.atmategateway.services.JWTTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException; // Para erros HTTP
import jakarta.validation.Valid; // Se usares validação nos DTOs

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTTokenService jwtTokenProvider;

    @PostMapping("/login")
    @Operation(
            summary = "Realizar o login de um utilizador",
            description = "Endpoint que recebe um email e uma password e faz o login de um utilizador se estes estiverem corretos. É criado um token JWT."
    )
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email e password são obrigatórios.");
        }

        Optional<User> userOptional = userService.findByEmail(loginRequest.getEmail());
        if (userOptional.isEmpty()) {
            // Usar uma mensagem genérica para não revelar se o email existe ou não
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.");
        }

        User user = userOptional.get();

        // Verificar a password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.");
        }

        // Gerar o token JWT
        String token = jwtTokenProvider.generateToken(user);

        // Preparar os detalhes do utilizador para a resposta
        UserDetailsResponse userDetails = new UserDetailsResponse(user.getUsername(), user.getEmail());

        // Retornar a resposta com o token e os detalhes do utilizador
        LoginResponse loginResponse = new LoginResponse(token, userDetails);
        return ResponseEntity.ok(loginResponse);
    }

    // NOVO ENDPOINT DE REGISTO
    @PostMapping("/create")
    @Operation(
            summary = "Criar um utilizador",
            description = "Endpoint que a informação do novo utilizador (email, password, username) e cria o utilizador."
    )
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest registerRequest) {
        try {

            User newUser = new User();
            newUser.setUsername(registerRequest.getUsername());
            newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            newUser.setEmail(registerRequest.getEmail());

            newUser = userService.createUser(newUser);

            UserDetailsResponse userDetails = new UserDetailsResponse(newUser.getUsername(), newUser.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(userDetails);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro durante o registo.");
        }
    }

}
