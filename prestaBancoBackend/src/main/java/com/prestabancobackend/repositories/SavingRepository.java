package com.prestabancobackend.repositories;

import com.prestabancobackend.entities.SavingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingRepository extends JpaRepository<SavingEntity, Long> {
}
