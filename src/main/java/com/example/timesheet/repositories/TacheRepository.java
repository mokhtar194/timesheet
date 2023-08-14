package com.example.timesheet.repositories;

import com.example.timesheet.entities.Tache;
import com.example.timesheet.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface TacheRepository extends JpaRepository<Tache,Long> {
    Page<Tache> findByTitreContains(String kw, Pageable pageable);
    Tache findByIdIs(Long kw);
    Page<Tache> findByUserIs(User users, Pageable pageable);
    Page<Tache> findByTitreContainsAndUserIn(String keyword, List<Tache> userTaches, Pageable pageable);
    Page<Tache> findByTitreContainsAndDateDebutGreaterThanEqual(String keyword,Date date,  Pageable pageable);
    Page<Tache> findByTitreContainsAndDateFinLessThanEqual(String keyword,Date date,  Pageable pageable);
    Page<Tache> findByTitreContainsAndDateDebutGreaterThanEqualAndDateFinLessThanEqual(String keyword,Date date,Date date1,  Pageable pageable);
    Page<Tache> findByUserIsOrTitreContainsAndUserIsOrDateDebutGreaterThanAndDateFinLessThan(
            User users, String keyword, User users2, Date startDate, Date endDate, Pageable pageable);
    Page<Tache> findByTitreContainsOrDateDebutGreaterThanAndDateFinLessThan(
            String keyword, Date startDate, Date endDate, Pageable pageable);
    Page<Tache> findByDateDebutBetweenAndTitreContains(LocalDate dateDebut, LocalDate dateFin, String kw, Pageable pageable);
}
