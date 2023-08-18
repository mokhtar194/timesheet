package com.example.timesheet.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTaskSummaryDTO {
    @Id

    private String email;
    private int nonCommenceCount;
    private int enAttenteCount;
    private int enCoursCount;
    private int termineCount;

}
