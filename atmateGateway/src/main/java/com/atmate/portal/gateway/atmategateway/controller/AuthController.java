package com.atmate.portal.gateway.atmategateway.controller;

import com.atmate.portal.gateway.atmategateway.database.dto.CreateUserRequest;
import com.atmate.portal.gateway.atmategateway.database.dto.LoginRequest;
import com.atmate.portal.gateway.atmategateway.database.dto.LoginResponse;
import com.atmate.portal.gateway.atmategateway.database.dto.UserDetailsDTO;
import com.atmate.portal.gateway.atmategateway.database.entitites.User;
import com.atmate.portal.gateway.atmategateway.database.services.UserService;
import com.atmate.portal.gateway.atmategateway.services.JWTTokenService;
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
@RequestMapping("/auth") // Endpoint base para autenticação
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTTokenService jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        // 1. Validar o pedido (o @Valid faz isso se tiveres anotações no DTO)
        if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email e password são obrigatórios.");
        }

        // 2. Encontrar o utilizador pelo email
        Optional<User> userOptional = userService.findByEmail(loginRequest.getEmail());
        if (userOptional.isEmpty()) {
            // Usar uma mensagem genérica para não revelar se o email existe ou não
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.");
        }

        User user = userOptional.get();

        // 3. Verificar a password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.");
        }

        // 4. Gerar o token JWT
        String token = jwtTokenProvider.generateToken(user);

        // 5. Preparar os detalhes do utilizador para a resposta
        UserDetailsDTO userDetails = new UserDetailsDTO( user.getUsername(), user.getEmail());

        // 6. Retornar a resposta com o token e os detalhes do utilizador
        LoginResponse loginResponse = new LoginResponse(token, userDetails);
        return ResponseEntity.ok(loginResponse);
    }

    // NOVO ENDPOINT DE REGISTO
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest registerRequest) {
        try {

            User newUser = new User();
            newUser.setUsername(registerRequest.username());
            newUser.setPassword(passwordEncoder.encode(registerRequest.password()));
            newUser.setEmail(registerRequest.email());

            newUser = userService.createUser(newUser);

            UserDetailsDTO userDetails = new UserDetailsDTO(newUser.getUsername(), newUser.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(userDetails);
        } catch (Exception e) {
            // Logar o erro e.printStackTrace(); ou com um logger
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro durante o registo.");
        }
    }

}
