package com.atmate.portal.gateway.atmategateway.database.services;

import com.atmate.portal.gateway.atmategateway.database.entitites.Contact;
import com.atmate.portal.gateway.atmategateway.database.repos.ContactRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    @Autowired
    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    // Criar um novo contato
    public Contact createContact(Contact contact) {
        return contactRepository.save(contact);
    }

    // Ler todos os contatos
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    // Ler um contato por ID
    public Optional<Contact> getContactById(Integer id) {
        return contactRepository.findById(id);
    }

    // Atualizar um contato
    public Contact updateContact(Integer id, Contact contactDetails) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contato não encontrado com ID: " + id));

        contact = contactDetails;

        return contactRepository.save(contact);
    }

    @Transactional
    // Deletar um contato
    public void deleteContactByClientId(Integer id) {
        if (!contactRepository.existsContactByClientId(id)) {
            System.out.println("Contactos não encontrados com ID: " + id);
        }

        contactRepository.deleteContactByClientId(id);
    }
}
