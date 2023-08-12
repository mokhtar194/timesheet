package com.example.timesheet.service;

import com.example.timesheet.entities.Role;
import com.example.timesheet.entities.User;

import java.util.List;

public interface UserService {
    User addNewUser(User collab,String confirmPassword);
    void removeUser(String email);

    Role addNewRole(String role);
    void addRoleToUser(String email, String[] roles);

    void removeRoleFromUser(String email , String role);
    User loadUserByEmail(String email);
}
