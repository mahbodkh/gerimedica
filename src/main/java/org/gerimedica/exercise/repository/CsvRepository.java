package org.gerimedica.exercise.repository;

import org.gerimedica.exercise.domain.CsvEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CsvRepository extends JpaRepository<CsvEntity, Long> {
    Optional<CsvEntity> findByCode(String code);
}
