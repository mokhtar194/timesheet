package com.example.timesheet.web;


import com.example.timesheet.entities.Role;
import com.example.timesheet.entities.Tache;
import com.example.timesheet.entities.User;
import com.example.timesheet.enums.Etats;
import com.example.timesheet.repositories.TacheRepository;
import com.example.timesheet.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@AllArgsConstructor
public class TacheController {
    private TacheRepository tacheRepository;
    private UserRepository userRepository;
    @GetMapping(path="/index")
    public  String taches(Model model, @RequestParam(name="page",defaultValue = "0")int page
            , @RequestParam(name="size",defaultValue = "5")int size,
                          @AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam(name="keyword",defaultValue = "")String keyword)
    {
        User user = userRepository.findByEmail(userDetails.getUsername());
        List<Tache> userTaches = user.getTaches(); // Assuming you have a relationship like this
        List<Tache> filteredTaches = new ArrayList<>();
        Role r=new Role("COLLAB");
        Role r1=new Role("ADMIN");
        if (user.getRoles().contains(r) && !user.getRoles().contains(r1) ) { // Assuming role name is used for identification
           // Page<Tache> pageTaches = tacheRepository.findByTitreContainsAndUserIn(keyword, userTaches, PageRequest.of(page, size));
            List<Tache> pageTaches =user.getTaches();
            filteredTaches.addAll(pageTaches);
            model.addAttribute("pages", new int[pageTaches.size()]);
            model.addAttribute("currentPage", page);
        } else if
        (user.getRoles().contains(r1)) {
            Page<Tache> pageTaches = tacheRepository.findByTitreContains(keyword, PageRequest.of(page, size));
            filteredTaches.addAll(pageTaches.getContent());
            model.addAttribute("pages", new int[pageTaches.getTotalPages()]);
            model.addAttribute("currentPage", page);
        }

        model.addAttribute("listTaches", filteredTaches);
        model.addAttribute("keyword", keyword);
        return "tache";
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
        tacheRepository.save(tache);
        userRepository.save(user);

        return "redirect:/index";
    }
    @GetMapping("/edit")

    public String edit(@RequestParam(name = "id") Long id, Model model){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Tache tache=tacheRepository.findById(id).get();
        model.addAttribute("tache",tache);
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
