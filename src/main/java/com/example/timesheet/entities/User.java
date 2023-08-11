package com.example.timesheet.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    @Column(
            length = 50
    )
    private String nom;
    @Column(
            length = 50
    )
    private String prenom;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;
    @OneToMany
    private List<Tache> taches;

    @Column(
            length = 100
    )
    private String email;
    private String password;

}
