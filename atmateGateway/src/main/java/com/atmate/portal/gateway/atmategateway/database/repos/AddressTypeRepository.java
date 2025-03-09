package com.atmate.portal.gateway.atmategateway.database.repos;

import com.atmate.portal.gateway.atmategateway.database.entitites.AddressType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressTypeRepository extends JpaRepository<AddressType, Integer> {



}
