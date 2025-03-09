package com.atmate.portal.gateway.atmategateway.database.services;

import com.atmate.portal.gateway.atmategateway.database.entitites.Tax;
import com.atmate.portal.gateway.atmategateway.database.repos.TaxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaxService {

    private final TaxRepository taxRepository;

    @Autowired
    public TaxService(TaxRepository taxRepository) {
        this.taxRepository = taxRepository;
    }

    // Criar um novo imposto
    public Tax createTax(Tax tax) {
        return taxRepository.save(tax);
    }

    // Ler todos os impostos
    public List<Tax> getAllTaxes() {
        return taxRepository.findAll();
    }

    // Ler um imposto por ID
    public Optional<Tax> getTaxById(Integer id) {
        return taxRepository.findById(id);
    }

    // Atualizar um imposto
    public Tax updateTax(Integer id, Tax taxDetails) {
        Tax tax = taxRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imposto não encontrado com ID: " + id));

        // Atualizar os campos do imposto
        tax.setTaxType(taxDetails.getTaxType());
        tax.setTaxData(taxDetails.getTaxData());
        // Adicione outros campos conforme necessário

        return taxRepository.save(tax);
    }

    // Deletar um imposto
    public void deleteTax(Integer id) {
        if (!taxRepository.existsById(id)) {
            throw new RuntimeException("Imposto não encontrado com ID: " + id);
        }
        taxRepository.deleteById(id);
    }
}
