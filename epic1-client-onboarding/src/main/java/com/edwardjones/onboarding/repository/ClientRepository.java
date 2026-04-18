package com.edwardjones.onboarding.repository;

import com.edwardjones.onboarding.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Client entity data access.
 * Story Reference: MAP-8, MAP-10
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Finds a client by their email address.
     * @param email the client's email
     * @return Optional containing the client if found
     */
    Optional<Client> findByEmail(String email);

    /**
     * Checks if a client exists with the given email.
     * @param email the email to check
     * @return true if exists
     */
    boolean existsByEmail(String email);

    /**
     * Finds all clients assigned to a specific financial advisor.
     * @param advisorId the advisor's ID
     * @return list of clients
     */
    List<Client> findByAssignedAdvisorId(Long advisorId);

    /**
     * Finds all clients by their onboarding status.
     * @param status the client status
     * @return list of clients
     */
    List<Client> findByStatus(Client.ClientStatus status);

    /**
     * Finds clients not yet assigned to an advisor.
     * @return list of unassigned clients
     */
    @Query("SELECT c FROM Client c WHERE c.assignedAdvisorId IS NULL AND c.status = 'PENDING'")
    List<Client> findUnassignedClients();

    /**
     * Counts clients assigned to a specific advisor.
     * @param advisorId the advisor's ID
     * @return count of assigned clients
     */
    @Query("SELECT COUNT(c) FROM Client c WHERE c.assignedAdvisorId = :advisorId")
    Long countByAdvisorId(@Param("advisorId") Long advisorId);
}
