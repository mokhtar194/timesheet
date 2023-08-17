package com.example.timesheet.service;

import com.example.timesheet.entities.Role;
import com.example.timesheet.entities.Tache;
import com.example.timesheet.entities.User;
import com.example.timesheet.enums.Etats;
import com.example.timesheet.repositories.TacheRepository;
import com.example.timesheet.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;


@Service
@Transactional
@AllArgsConstructor
@NoArgsConstructor
public class ExcelService {
    private PasswordEncoder passwordEncoder;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public void exportTacheToExcel(List<Tache> taches, OutputStream outputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Taches");
        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd")); // Set your desired date format here
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Titre");
        headerRow.createCell(2).setCellValue("Description");

        headerRow.createCell(3).setCellValue("Collab");
        headerRow.createCell(4).setCellValue("DateDebut");

        headerRow.createCell(5).setCellValue("DateFin");

        headerRow.createCell(6).setCellValue("EtatAvancement");

        headerRow.createCell(7).setCellValue("HeureTravaillees");



        // Add other titles

        int rowIdx = 1;
        for (Tache tache : taches) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(tache.getId());
            row.createCell(1).setCellValue(tache.getTitre());
            row.createCell(2).setCellValue(tache.getDescription());
            row.createCell(3).setCellValue(tache.getUser().getEmail());
            Cell dateCellDebut = row.createCell(4);
            dateCellDebut.setCellValue(tache.getDateDebut());
            dateCellDebut.setCellStyle(dateCellStyle);
           // row.createCell(4).setCellValue(tache.getDateDebut());
            Cell dateCellFin = row.createCell(5);
            dateCellFin.setCellValue(tache.getDateFin());
            dateCellFin.setCellStyle(dateCellStyle);
           // row.createCell(5).setCellValue(tache.getDateFin());
            row.createCell(6).setCellValue(tache.getEtatAvancement().name());
            row.createCell(7).setCellValue(tache.getHeureTravaillees());
            // Add more cell creations for other attributes

            // Example: row.createCell(2).setCellValue(tache.getDateDebut());
        }

        workbook.write(outputStream);
        workbook.close();
    }

    public void importTacheFromExcel(InputStream inputStream,UserRepository userRepository,TacheRepository tacheRepository) throws IOException {

        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);


        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue; // Skip empty rows
            }
            // Retrieve cell values and create Tache objects
            Long id = (long) row.getCell(0).getNumericCellValue();
            String titre = row.getCell(1).getStringCellValue();
            String Description = row.getCell(2).getStringCellValue();
            String getEmail = row.getCell(3).getStringCellValue();
            Date dateDebut = row.getCell(4).getDateCellValue();
            Date dateFin = row.getCell(5).getDateCellValue();


            String EtatAvancement = row.getCell(6).getStringCellValue();
            int HeureTravaillees = (int)row.getCell(7).getNumericCellValue();
            System.out.println(titre+Description+getEmail+dateDebut+dateFin+EtatAvancement+HeureTravaillees);


            Tache tache = new Tache();

            tache.setTitre(titre);
            tache.setDescription(Description);
            System.out.println("*********************************************************************");
            System.out.println(getEmail);

            User user=userRepository.findByEmail(getEmail);
            tache.setUser(user);


            tache.setDateDebut(dateDebut);
            tache.setDateFin(dateFin);
            tache.setEtatAvancement(Etats.valueOf(EtatAvancement));
            tache.setHeureTravaillees(HeureTravaillees);
            // Set other attributes

            // Example: tache.setDateDebut(dateDebut);


            user.getTaches().add(tache);

            tacheRepository.save(tache);
            userRepository.save(user);
        }

        workbook.close();
    }
    public void exportCollabToExcel(List<User> users, OutputStream outputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Collabs");
        System.out.println("*************************************************");

        System.out.println(users.size());
        System.out.println("*************************************************");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Nom");
        headerRow.createCell(2).setCellValue("Prenom");
        headerRow.createCell(4).setCellValue("Email");
        headerRow.createCell(3).setCellValue("Role");
        headerRow.createCell(5).setCellValue("Tache");
        headerRow.createCell(6).setCellValue("password");



        // Add other titles

        int rowIdx = 1;
        for (User user : users) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getNom());
            row.createCell(2).setCellValue(user.getPrenom());
            row.createCell(3).setCellValue(user.getEmail());
            Role roles=new Role("ADMIN");
            if (user.getRoles().contains(roles)) {
                row.createCell(4).setCellValue("ADMIN");
            }else {row.createCell(4).setCellValue("COLLAB");}
            row.createCell(5).setCellValue(user.getTaches().size());
            row.createCell(6).setCellValue(user.getPassword());
            // Add more cell creations for other attributes

            // Example: row.createCell(2).setCellValue(tache.getDateDebut());
        }

        workbook.write(outputStream);
        workbook.close();
    }
    public void importCollabFromExcel(InputStream inputStream,UserRepository userRepository) throws IOException {

        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);


        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue; // Skip empty rows
            }
            // Retrieve cell values and create Tache objects
            Long id = (long) row.getCell(0).getNumericCellValue();
            String nom = row.getCell(1).getStringCellValue();
            String prenom = row.getCell(2).getStringCellValue();
            String email = row.getCell(3).getStringCellValue();
            String role = row.getCell(4).getStringCellValue();
            String password = row.getCell(6).getStringCellValue();


            User user = new User();
            user.setNom(nom);
            user.setPrenom(prenom);
            user.setEmail(email);
            if(role=="ADMIN"){
                List<Role> rols=new ArrayList<>();
                Role r=new Role("ADMIN");
                Role r2=new Role("COLLAB");
                rols.add(r);
                rols.add(r2);
                user.setRoles(rols);}else{
                List<Role> rols=new ArrayList<>();

                Role r2=new Role("COLLAB");

                rols.add(r2);
                user.setRoles(rols);

            }
            if(password==null){

                user.setPassword(passwordEncoder.encode("1234"));

            }else{
                user.setPassword(password);
            }

            user.setTaches(null);


            userRepository.save(user);
        }

        workbook.close();
    }
}
