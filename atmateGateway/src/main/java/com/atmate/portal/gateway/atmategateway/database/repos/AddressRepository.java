package com.atmate.portal.gateway.atmategateway.database.repos;

import com.atmate.portal.gateway.atmategateway.database.entitites.Address;
import com.atmate.portal.gateway.atmategateway.database.entitites.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    void deleteAddressByClientId(int id);

    boolean existsAddressByClientId(int id);

    List<Address> getAddressesByClient(Client client);

}
