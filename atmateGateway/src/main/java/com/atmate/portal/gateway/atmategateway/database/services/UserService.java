package com.atmate.portal.gateway.atmategateway.database.services;

import com.atmate.portal.gateway.atmategateway.database.entitites.User;
import com.atmate.portal.gateway.atmategateway.database.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    // Novo método para registar utilizador
    @Transactional // Garante que a operação é atómica
    public User createUser(User user) throws Exception {
        // Verificar se o email já existe
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("Já existe uma conta com o email: " + user.getEmail());
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
             throw new Exception("Nome de utilizador já em uso: " + user.getUsername());
        }

        return userRepository.save(user);
    }


}
