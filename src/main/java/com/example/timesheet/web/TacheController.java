package com.example.timesheet.web;


import com.example.timesheet.entities.Role;
import com.example.timesheet.entities.Tache;
import com.example.timesheet.entities.User;
import com.example.timesheet.enums.Etats;
import com.example.timesheet.repositories.TacheRepository;
import com.example.timesheet.repositories.UserRepository;
import com.example.timesheet.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
@AllArgsConstructor
public class TacheController {
    private TacheRepository tacheRepository;
    private UserRepository userRepository;
    @GetMapping(path="/index")
    public  String taches(Model model, @RequestParam(name="page",defaultValue = "0")int page
            , @RequestParam(name="size",defaultValue = "6")int size,
                          @AuthenticationPrincipal UserDetails userDetails,
                          @RequestParam(name = "startDate", defaultValue = "") String startDate,
                          @RequestParam(name = "endDate", defaultValue = "") String endDate,
                            @RequestParam(name="keyword",defaultValue = "")String keyword)
    {
        User user = userRepository.findByEmail(userDetails.getUsername());
        LocalDate parsedStartDate = startDate.isEmpty() ? null : LocalDate.parse(startDate);
        Date convertedStartDate = parsedStartDate != null
                ? Date.from(parsedStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                : null;
        LocalDate parsedEndDate = endDate.isEmpty() ? null : LocalDate.parse(endDate);
        Date convertedEndDate =parsedEndDate != null
                ? Date.from(parsedEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                : null;
        List<Tache> userTaches = user.getTaches(); // Assuming you have a relationship like this
        List<Tache> filteredTaches = new ArrayList<>();
        Role r=new Role("COLLAB");
        Role r1=new Role("ADMIN");
        if (user.getRoles().contains(r) && !user.getRoles().contains(r1)) {
            System.out.println("********************************************************************************************************");
            //Page<Tache> pageTaches = tacheRepository.findByUserInOrTitreContainsAndUserInOrDateDebutGreaterThanEqualAndDateFinLessThanEqual(
                //    userTaches,keyword, userTaches, convertedStartDate, convertedEndDate, PageRequest.of(page, size));
            Page<Tache> pageTaches =tacheRepository.findByUserIsOrTitreContainsAndUserIsOrDateDebutGreaterThanAndDateFinLessThan(
                    user, keyword, user, convertedStartDate, convertedEndDate, PageRequest.of(page, size));
            System.out.println("********************************************************************************************************");
            filteredTaches.addAll(pageTaches.getContent());
            model.addAttribute("pages", new int[pageTaches.getTotalPages()]);
            model.addAttribute("currentPage", page);
        } else if (user.getRoles().contains(r1)) {
            if (convertedEndDate==null && convertedStartDate!=null ){
                Page<Tache> pageTaches = tacheRepository.findByTitreContainsAndDateDebutGreaterThanEqual(
                        keyword,convertedStartDate, PageRequest.of(page, size));
                filteredTaches.addAll(pageTaches.getContent());
                model.addAttribute("pages", new int[pageTaches.getTotalPages()]);
                model.addAttribute("currentPage", page);
            } else if (convertedEndDate!=null && convertedStartDate==null) {
                Page<Tache> pageTaches = tacheRepository.findByTitreContainsAndDateFinLessThanEqual(
                        keyword,convertedEndDate, PageRequest.of(page, size));
                filteredTaches.addAll(pageTaches.getContent());
                model.addAttribute("pages", new int[pageTaches.getTotalPages()]);
                model.addAttribute("currentPage", page);
            } else if (convertedEndDate!=null && convertedStartDate!=null) {
                Page<Tache> pageTaches = tacheRepository.findByTitreContainsAndDateDebutGreaterThanEqualAndDateFinLessThanEqual(
                        keyword,convertedStartDate,convertedEndDate, PageRequest.of(page, size));
                filteredTaches.addAll(pageTaches.getContent());
                model.addAttribute("pages", new int[pageTaches.getTotalPages()]);
                model.addAttribute("currentPage", page);
            } else {Page<Tache> pageTaches = tacheRepository.findByTitreContains(
                    keyword, PageRequest.of(page, size));
                filteredTaches.addAll(pageTaches.getContent());
                model.addAttribute("pages", new int[pageTaches.getTotalPages()]);

                model.addAttribute("currentPage", page);}

        }

        model.addAttribute("listTaches", filteredTaches);
        model.addAttribute("keyword", keyword);
        return "tache1";
    }

    @GetMapping("/delete")

    public String delete(Long id,String keyword,int page){
        tacheRepository.deleteById(id);
        return "redirect:/index?page="+page+"&keyword="+keyword;
    }
    @GetMapping("/form")

    public String form(Model model ){
        model.addAttribute("tache",new Tache());
        model.addAttribute("TachEtat", Etats.values());
        model.addAttribute("Collabs", userRepository.findAll());
        return "form";
    }
    @PostMapping("/save")

    public String save(@Valid Tache tache, BindingResult bindingResult,@RequestParam("coll") String coll){
        if (bindingResult.hasErrors()) return "form";
        User user=userRepository.findByEmail(coll);

        tache.setUser(user);

        user.getTaches().add(tache);
        if (tache.getId()!=null && tache.getTitre()==null){
            Tache tacheE =   tacheRepository.findByIdIs(tache.getId());
            tache.setTitre(tacheE.getTitre());
            tache.setDescription(tacheE.getDescription());
            tache.setDateDebut(tacheE.getDateDebut());
            tache.setDateFin(tacheE.getDateFin());
        }
        tacheRepository.save(tache);
        userRepository.save(user);

        return "redirect:/index";
    }

    @GetMapping("/edit")

    public String edit(@RequestParam(name = "id") Long id, Model model){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Tache tache=tacheRepository.findById(id).get();
        model.addAttribute("tache",tache);
        model.addAttribute("selectedcollab",tache.getUser().getEmail());
        List<User> Collabs = userRepository.findAll();
        Collabs.remove(tache.getUser());
        model.addAttribute("Collabs", Collabs);
        User user=userRepository.findByEmail(tache.getUser().getEmail());
        user.getTaches().remove(tache);
        userRepository.save(user);
        List<Etats> etats= Arrays.asList(Etats.values());
        List<String> etats1=new ArrayList<>();
        for (Etats z:etats
             ) {
           etats1.add(z.toString());
        }

       etats1.remove(tache.getEtatAvancement().toString());
        model.addAttribute("etat",tache.getEtatAvancement());
        model.addAttribute("TachEtat",etats1);
        model.addAttribute("formattedDateDebut", dateFormat.format(tache.getDateDebut()));
        model.addAttribute("formattedDateFin", dateFormat.format(tache.getDateFin()));

        return "edit";
    }
    @GetMapping("/")
    public String home(){

        return "redirect:/index";
    }
    @GetMapping("/taches")
    @ResponseBody
    public List<Tache> listTaches(){
        return tacheRepository.findAll();
    }

}
