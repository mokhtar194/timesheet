package com.example.timesheet.service;

import com.example.timesheet.entities.Role;
import com.example.timesheet.entities.User;

public interface UserService {
    User addNewUser(String nom, String prenom,  String email,String password, String confirmPassword);
    void removeUser(String email);

    Role addNewRole(String role);
    void addRoleToUser(String email,String role);

    void removeRoleFromUser(String email , String role);
    User loadUserByEmail(String email);
}
