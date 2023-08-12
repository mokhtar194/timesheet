package com.example.timesheet.service;

import com.example.timesheet.entities.Role;
import com.example.timesheet.entities.User;
import com.example.timesheet.repositories.RoleRepository;
import com.example.timesheet.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository UserRepository;
    private RoleRepository RoleRepository;
    private PasswordEncoder passwordEncoder;



    @Override
    public void removeUser(String email){
        User appUser = UserRepository.findByEmail(email);
        if (appUser == null) {
            throw new RuntimeException("User not found");
        }
        UserRepository.deleteByEmail(email);
    }
    @Override
    public User addNewUser(String nom, String prenom,  String email,String password, String confirmPassword) {
        User user = UserRepository.findByEmail(email);
        if(user!=null)throw new RuntimeException("this user already exist");
        if (!password.equals(confirmPassword)) throw new RuntimeException("Password not match");
        user= user.builder()
                .id(UUID.randomUUID().getMostSignificantBits())
                .nom(nom)
                .prenom(prenom)
                .email(email)
                .password(passwordEncoder.encode(password))
                .email(email)
                .build();
        User savedUser= UserRepository.save(user);
        return savedUser;
    }

    @Override
    public Role addNewRole(String role1) {
        Role role=RoleRepository.findById(role1).orElse(null);
        if(role!=null)throw new RuntimeException("this role already exist");
        role=Role.builder()
                .role(role1)
                .build();

        return RoleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String email, String role1) {
        User user=UserRepository.findByEmail(email);
        Role role=RoleRepository.findById(role1).get();
        user.getRoles().add(role);


    }

    @Override
    public void removeRoleFromUser(String username, String role) {

    }



    @Override
    public User loadUserByEmail(String email) {
        return UserRepository.findByEmail(email);
    }
}
