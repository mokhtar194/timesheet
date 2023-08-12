package com.example.timesheet.entities;

import com.example.timesheet.enums.Etats;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tache {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    @Column(
            length = 50
    )
    private String titre;
    @Column(
            length = 300
    )
    private String description;
    @ManyToOne
    private User user;
    @Column(
            length = 50
    )
    @Temporal(TemporalType.DATE)
    private Date dateDebut;
    @Temporal(TemporalType.DATE)
    private Date dateFin;


    @Enumerated(value = EnumType.STRING)
    private Etats etatAvancement;

    private int heureTravaillees;

}
