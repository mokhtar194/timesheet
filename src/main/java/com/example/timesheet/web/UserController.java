package com.example.timesheet.web;

import com.example.timesheet.entities.Role;
import com.example.timesheet.entities.Tache;
import com.example.timesheet.entities.User;
import com.example.timesheet.entities.UserTaskSummaryDTO;
import com.example.timesheet.enums.Etats;
import com.example.timesheet.repositories.RoleRepository;
import com.example.timesheet.repositories.TacheRepository;
import com.example.timesheet.repositories.UserRepository;
import com.example.timesheet.service.ExcelService;
import com.example.timesheet.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

@Controller
@AllArgsConstructor

public class UserController {

    private UserRepository userRepository;

    private TacheRepository tacheRepository;
    private UserServiceImpl userService;
    private ExcelService excelService;
    private PasswordEncoder passwordEncoder;

    static int EA=0;
    static int EC=0;
    static int NC=0;
    static int T=0;
    private RoleRepository roleRepository;
    @GetMapping(path="/admin/collabs")
    public  String users(Model model, @RequestParam(name="page",defaultValue = "0")int page
            , @RequestParam(name="size",defaultValue = "9")int size,
                          @RequestParam(name="keyword",defaultValue = "")String keyword)
    {
        Page<User> pageUsers=userRepository.findByEmailContainsOrNomContainsOrPrenomContains(keyword,keyword,keyword,PageRequest.of(page,size));

        model.addAttribute("listCollabs",pageUsers.getContent());
        model.addAttribute("pages",new int[pageUsers.getTotalPages()]);
        model.addAttribute("currentPage",page);
        model.addAttribute("keyword",keyword);
        return "collab";
    }
    @GetMapping(path="/home")
    public  String home(Model model,@RequestParam(name = "name", defaultValue = "") String name)
    {

        List<User> listCollabs = userRepository.findAll();
        User userad=userRepository.findByEmail(name);
        boolean ad=false;

        for (Role role:userad.getRoles())
        {
            if (role.getRole().equals("ADMIN"))
            {
                ad=true;
            }
        }

        if(ad) {
            List<UserTaskSummaryDTO> summaries = new ArrayList<>();


            for (User user : listCollabs) {
                if (user.getTaches().size() > 0) {


                    UserTaskSummaryDTO summary = new UserTaskSummaryDTO();
                    summary.setEmail(user.getEmail());
                    Long a = user.getTaches().stream().filter(t -> t.getEtatAvancement() == Etats.NON_COMMENCE).count();
                    Long r = user.getTaches().stream().filter(t -> t.getEtatAvancement() == Etats.EN_ATTENTE).count();
                    Long z = user.getTaches().stream().filter(t -> t.getEtatAvancement() == Etats.EN_COURS).count();
                    Long b = user.getTaches().stream().filter(t -> t.getEtatAvancement() == Etats.TERMINE).count();

                    if ((r + a + z + b) == 0) {

                        summary.setNonCommenceCount(0);
                        summary.setEnAttenteCount(0);
                        summary.setEnCoursCount(0);
                        summary.setTermineCount(0);

                    } else {
                        long aresult = (a * 100) / (r + a + b + z);
                        summary.setNonCommenceCount((int) aresult);
                        long rressult = (r * 100 / (r + a + z + b));
                        summary.setEnAttenteCount((int) rressult);
                        long zresult = (z * 100) / (r + a + z + b);
                        summary.setEnCoursCount((int) zresult);
                        long bresult = (b * 100) / (r + a + z + b);
                        summary.setTermineCount((int) bresult);
                    }


                    summaries.add(summary);

                }
            }
            List<Tache> listtache = tacheRepository.findAll();
            model.addAttribute("summaries", summaries);
            model.addAttribute("summariesCount", listCollabs.size());
            model.addAttribute("listtache", listtache.size());
            model.addAttribute("chartData", UserHeurTravaill());

            model.addAttribute("chartData1", graphData());

            return "home3";
        }
        else {
        List<Tache>listtacheorder= tacheRepository.findByUserIsOrderByDateFin(userad);
            List<Tache>listtacheorder1=new ArrayList<>();
        for (Tache tache:listtacheorder)
        {
            if (!tache.getEtatAvancement().name().equals("TERMINE"))
            {
                listtacheorder1.add(tache);
            }
        }
            model.addAttribute("listtache",userad.getTaches().size());
            model.addAttribute("nbheurs",nbHeuretravaille(userad));
            model.addAttribute("listtacheorder", listtacheorder1);
            model.addAttribute("chartData",UserHeurTravaillcollab(userad)) ;

            model.addAttribute("chartData1", graphDatacollab(userad));

            return "homecollab";
        }
    }


    @GetMapping("/admin/deleteCollab")

    public String delete(Long id,String keyword,int page){
        User user=userRepository.findByIdIs(id);
        user.setRoles(null);
        user.setTaches(null);
        userRepository.save(user);
        userRepository.deleteById(id);
        return "redirect:/admin/collabs?page="+page+"&keyword="+keyword;
    }
    @GetMapping("/admin/formCollab")

    public String form(Model model ){
        model.addAttribute("collab",new User());
        model.addAttribute("Roles",roleRepository.findAll());

        return "formCollab1";
    }

    @PostMapping("/admin/saveCollab")

    public String save(@Valid User collab, BindingResult bindingResult,@RequestParam("confirmpassword") String confirmPassword, @RequestParam("roles") String[] roles){

        if (bindingResult.hasErrors()) return "formCollab";

        userService.addNewUser(collab,confirmPassword);
        if(Arrays.stream(roles).toList().contains("ADMIN")){

            String[] array = {"ADMIN", "COLLAB"};
            userService.addRoleToUser(collab.getEmail(),array);

        } else  {
            userService.addRoleToUser(collab.getEmail(),roles);
        }

        return "redirect:/admin/collabs";
    }
    @PostMapping("/admin/EsaveCollab")

    public String Esave(@Valid User collab, BindingResult bindingResult, @RequestParam("roles") String[] roles,@RequestParam(name = "confirmpassword")String confirmpassword,
                        @RequestParam(name = "password1")String password,@RequestParam(name = "keyword")String keyword,@RequestParam(name = "currentPage")int page){

        if (bindingResult.hasErrors()) return "formCollab";
        System.out.println("outif");

        System.out.println(password+confirmpassword);
        if (!password.equals("") && !confirmpassword.equals("")){
            if(password.equals(confirmpassword))
            {
                System.out.println("INif");
                collab.setPassword(passwordEncoder.encode(password));
            }
        }
        User userdb = userRepository.findByIdIs(collab.getId());
        collab.setTaches(userdb.getTaches());


        userService.addRoleToUser(userdb.getEmail(),roles);
        userRepository.save(collab);

        return "redirect:/admin/collabs?page="+page+"&keyword="+keyword;
    }
    @GetMapping("/admin/editCollab")

    public String edit(@RequestParam(name = "id") Long id, Model model,String keyword,int page){
        User collab=userRepository.findById(id).get();
        model.addAttribute("collab",collab);
        List<Role> roles= new ArrayList<>();
        roles=roleRepository.findAll();
        roles.removeAll(collab.getRoles());
        model.addAttribute("role1",collab.getRoles());
        model.addAttribute("roles",roles);
        model.addAttribute("currentPage",page);
        model.addAttribute("keyword",keyword);
        return "editCollab1";
    }

    @GetMapping("/collabs")
    @ResponseBody
    public List<User> listcollabs(){
        return userRepository.findAll();
    }
    @GetMapping("/admin/exportCollab")
    public void exportCollabToExcel(HttpServletResponse response) throws IOException {
        List<User> user = userRepository.findAll();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=collabs.xlsx");

        OutputStream outputStream = response.getOutputStream();
        excelService.exportCollabToExcel(user, outputStream);
        outputStream.close();
    }


    private Map<String,Integer> graphData( ) {
        List<Tache> taches=tacheRepository.findAll();
        Map<String, Integer> Etatmap = new TreeMap<>();

        for (Tache t: taches)
        {
            if(t.getEtatAvancement().name().equals("EN_ATTENTE"))
            {
                EA++;
                System.out.println(EA);
            }
            else if(t.getEtatAvancement().name().equals("EN_COURS"))
            {
                EC++;
                System.out.println(EC);
            }
            else if(t.getEtatAvancement().name().equals("NON_COMMENCE"))
            {
                NC++;
                System.out.println(NC);
            }
            else if(t.getEtatAvancement().name().equals("TERMINE"))
            {
                T++;
                System.out.println(T);
            }}
        Etatmap.put("EN_ATTENTE",EA);
        Etatmap.put("EN_COURS",EC);
        Etatmap.put("NON_COMMENCE",NC);
        Etatmap.put("TERMINE",T);

        return Etatmap ;
    }
private Map<String, Integer> UserHeurTravaill()
{
    List<User> listusers = userRepository.findByTachesIsNotNull();
    Map<String, Integer> usermap = new TreeMap<>();

    for (User user:listusers)
    {
        int cpt=0;
        for (Tache tache:user.getTaches())
        {
            cpt=cpt+tache.getHeureTravaillees();

        }

        usermap.put(user.getNom()+" "+user.getPrenom(),cpt);
    }

    return usermap;
}



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private Map<String,Integer> graphDatacollab(User user ) {
        User user1=userRepository.findByIdIs(user.getId());
        Map<String, Integer> Etatmap = new TreeMap<>();

        for (Tache t: user1.getTaches())
        {
            if(t.getEtatAvancement().name().equals("EN_ATTENTE"))
            {
                EA++;
                System.out.println(EA);
            }
            else if(t.getEtatAvancement().name().equals("EN_COURS"))
            {
                EC++;
                System.out.println(EC);
            }
            else if(t.getEtatAvancement().name().equals("NON_COMMENCE"))
            {
                NC++;
                System.out.println(NC);
            }
            else if(t.getEtatAvancement().name().equals("TERMINE"))
            {
                T++;
                System.out.println(T);
            }}
        Etatmap.put("EN_ATTENTE",EA);
        Etatmap.put("EN_COURS",EC);
        Etatmap.put("NON_COMMENCE",NC);
        Etatmap.put("TERMINE",T);

        return Etatmap ;
    }

    private Map<String, Integer> UserHeurTravaillcollab(User user)
    {
        List<User> listusers = userRepository.findByTachesIsNotNull();
        User user1=userRepository.findByIdIs(user.getId());

        List<Tache> taches=user1.getTaches();
        Map<String, Integer> usermap = new TreeMap<>();

        for (Tache tache:taches)
        {
            usermap.put(tache.getTitre()+""+tache.getId(),tache.getHeureTravaillees());
        }


        return usermap;
    }
    private int nbHeuretravaille( User user)
    {
        User user1=userRepository.findByIdIs(user.getId());
        int cpt=0;
        for (Tache tache:user1.getTaches())
        {
            cpt=cpt+tache.getHeureTravaillees();
        }
        return cpt;
    }

}
