package com.example.timesheet.repositories;

import com.example.timesheet.entities.Tache;
import com.example.timesheet.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    Page<User> findByEmailContains(String kw, Pageable pageable);
    User findByEmail(String email);
    void deleteByEmail(String email);


}
