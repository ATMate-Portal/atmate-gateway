package com.atmate.portal.gateway.atmategateway.database.dto;

import com.atmate.portal.gateway.atmategateway.database.entitites.AtCredential;
import com.atmate.portal.gateway.atmategateway.database.entitites.Client;

public class ClientAtCredentialDTO {

    private Client client;
    private AtCredential atCredential;

    //Getters and setters
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public AtCredential getAtCredential() {
        return atCredential;
    }

    public void setAtCredential(AtCredential atCredential) {
        this.atCredential = atCredential;
    }
}

