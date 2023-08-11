package com.example.timesheet.repositories;

import com.example.timesheet.entities.Tache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TacheRepository extends JpaRepository<Tache,Long> {
    Page<Tache> findByTitreContains(String kw, Pageable pageable);
}
