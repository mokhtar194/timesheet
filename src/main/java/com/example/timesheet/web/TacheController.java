package com.example.timesheet.web;


import com.example.timesheet.entities.Tache;
import com.example.timesheet.enums.Etats;
import com.example.timesheet.repositories.TacheRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@AllArgsConstructor
public class TacheController {
    private TacheRepository tacheRepository;
    @GetMapping(path="/index")
    public  String taches(Model model, @RequestParam(name="page",defaultValue = "0")int page
            , @RequestParam(name="size",defaultValue = "5")int size,
                            @RequestParam(name="keyword",defaultValue = "")String keyword){
        Page<Tache> pageTaches=tacheRepository.findByTitreContains(keyword, PageRequest.of(page,size));
        model.addAttribute("listTaches",pageTaches.getContent());
        model.addAttribute("pages",new int[pageTaches.getTotalPages()]);
        model.addAttribute("currentPage",page);
        model.addAttribute("keyword",keyword);
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
        return "form";
    }
    @PostMapping("/save")

    public String save(@Valid Tache tache, BindingResult bindingResult){
        if (bindingResult.hasErrors()) return "form";
        tacheRepository.save(tache);
        return "redirect:/index";
    }
    @GetMapping("/edit")

    public String edit(@RequestParam(name = "id") Long id, Model model){
        Tache tache=tacheRepository.findById(id).get();
        model.addAttribute("tache",tache);
        model.addAttribute("TachEtat", Etats.values());
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
