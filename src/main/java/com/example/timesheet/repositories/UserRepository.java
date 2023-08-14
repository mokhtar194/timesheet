package com.example.timesheet.repositories;

import com.example.timesheet.entities.Tache;
import com.example.timesheet.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    Page<User> findByNomContains(String kw, Pageable pageable);
    Page<User> findByPrenomContains(String kw, Pageable pageable);
    Page<User> findByEmailContains(String kw, Pageable pageable);

    Page<User> findByEmailContainsOrNomContainsOrPrenomContains(String kw,String nom, String prenom,  Pageable pageable);
   // Page<User> findByEmailAndNomAndPrenomContains(String kw,String nom, String prenom ,Pageable pageable);
    User findByEmail(String email);
    void deleteByEmail(String email);

}

