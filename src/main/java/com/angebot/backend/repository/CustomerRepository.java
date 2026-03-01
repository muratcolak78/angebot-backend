package com.angebot.backend.repository;

import com.angebot.backend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    List<Customer> findByUserId(UUID userId);  // ← TÜM müşteriler
    Optional<Customer> findByUserIdAndId(UUID userId, UUID id);
    Optional<Customer> findByIdAndUserId(UUID id, UUID userId);
    boolean existsByUserIdAndEmail(UUID userId, String email);
}
