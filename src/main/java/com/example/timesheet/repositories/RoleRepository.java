package com.example.timesheet.repositories;

import com.example.timesheet.entities.Role;
import com.example.timesheet.entities.Tache;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,String> {
}
