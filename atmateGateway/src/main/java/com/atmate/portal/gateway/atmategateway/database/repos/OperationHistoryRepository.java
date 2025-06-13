package com.atmate.portal.gateway.atmategateway.database.repos;

import com.atmate.portal.gateway.atmategateway.database.dto.UniqueUserDTO;
import com.atmate.portal.gateway.atmategateway.database.entitites.OperationHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationHistoryRepository extends JpaRepository<OperationHistory, Integer> {

    @Query("SELECT o FROM OperationHistory o WHERE " +
            "(:userId IS NULL OR o.user.id = :userId) AND " +
            "(:actionCode IS NULL OR o.userAction = :actionCode) AND " +
            "(:startDate IS NULL OR o.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR o.createdAt <= :endDate) " +
            "ORDER BY o.createdAt DESC")
    Page<OperationHistory> findWithFilters(
            @Param("userId") Integer userId,
            @Param("actionCode") String actionCode,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);


    @Query("SELECT new com.atmate.portal.gateway.atmategateway.database.dto.UniqueUserDTO(o.user.id, o.user.username) " +
            "FROM OperationHistory o " +
            "GROUP BY o.user.id, o.user.username " +
            "ORDER BY o.user.username ASC")
    List<UniqueUserDTO> findDistinctUsers();

}

