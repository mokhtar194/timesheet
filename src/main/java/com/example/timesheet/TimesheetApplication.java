package com.example.timesheet;

import com.example.timesheet.entities.Role;
import com.example.timesheet.entities.Tache;
import com.example.timesheet.entities.User;
import com.example.timesheet.enums.Etats;
import com.example.timesheet.repositories.RoleRepository;
import com.example.timesheet.repositories.TacheRepository;
import com.example.timesheet.repositories.UserRepository;
import com.example.timesheet.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

@SpringBootApplication
public class TimesheetApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimesheetApplication.class, args);
    }
    @Bean
    CommandLineRunner commandLineRunnerUserDetails(UserService userService){
        return args -> {
           // User addNewUser(String nom, String prenom,  String email,String password, String confirmPassword);
           //  userService.addNewRole("COllAB");
           //  userService.addNewRole("ADMIN");
            // userService.addNewUser("admin", "admin","admin@gmail.com","1234", "1234");
           // userService.addRoleToUser("admin@gmail.com","ADMIN");
         //   userService.addNewUser("aziz1", "azizaziz1","aziz1@gmail.com","1234", "1234");
           // userService.addRoleToUser("aziz1@gmail.com","COllAB");
            // userService.addNewUser("user2","1234","user1@gmail.com","1234");
            //userService.removeUser("user3");
            // userService.addNewUser("user3","1234","user3@gmail.com","1234");
            //  userService.addNewUser("user4","1234","user4@gmail.com","1234");

            //  userService.addNewUser("admin","1234","admin@gmail.com","1234");
            // userService.addRoleToUser("user3","USER");
            //userService.addRoleToUser("user4","USER");
            // userService.addRoleToUser("admin","COLLAB");
              //userService.addRoleToUser("admin","ADMIN");

        };
    }
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
