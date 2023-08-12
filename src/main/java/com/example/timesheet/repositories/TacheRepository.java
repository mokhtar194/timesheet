package com.example.timesheet.repositories;

import com.example.timesheet.entities.Tache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TacheRepository extends JpaRepository<Tache,Long> {
    Page<Tache> findByTitreContains(String kw, Pageable pageable);
    Page<Tache> findByTitreContainsAndUserIn(String keyword, List<Tache> userTaches, Pageable pageable);
    Page<Tache> findByDateDebutBetweenAndTitreContains(LocalDate dateDebut, LocalDate dateFin, String kw, Pageable pageable);
}
