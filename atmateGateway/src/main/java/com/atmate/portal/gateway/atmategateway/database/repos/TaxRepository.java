package com.atmate.portal.gateway.atmategateway.database.repos;

import com.atmate.portal.gateway.atmategateway.database.entitites.Tax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaxRepository extends JpaRepository<Tax, Integer> {
    @Query("SELECT t FROM Tax t WHERE t.paymentDeadline BETWEEN :today AND :futureDate")
    List<Tax> findUrgentTaxes(@Param("today") LocalDate today, @Param("futureDate") LocalDate futureDate);

    boolean deleteTaxByClientId(int id);

    boolean existsTaxByClientId(int id);

}

