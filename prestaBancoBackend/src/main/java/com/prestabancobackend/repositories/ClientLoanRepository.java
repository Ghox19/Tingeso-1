package com.prestabancobackend.repositories;

import com.prestabancobackend.entities.ClientEntity;
import com.prestabancobackend.entities.ClientLoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientLoanRepository extends JpaRepository<ClientLoanEntity, Long> {
    List<ClientLoanEntity> findByClient(ClientEntity client);
}
