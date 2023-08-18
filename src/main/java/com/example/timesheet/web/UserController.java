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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@AllArgsConstructor
public class UserController {

    private UserRepository userRepository;
    private TacheRepository tacheRepository;
    private UserServiceImpl userService;
    private ExcelService excelService;
    private PasswordEncoder passwordEncoder;


    private RoleRepository roleRepository;
    @GetMapping(path="/admin/collabs")
    public  String users(Model model, @RequestParam(name="page",defaultValue = "0")int page
            , @RequestParam(name="size",defaultValue = "5")int size,
                          @RequestParam(name="keyword",defaultValue = "")String keyword)
    {
        Page<User> pageUsers=userRepository.findByEmailContainsOrNomContainsOrPrenomContains(keyword,keyword,keyword,PageRequest.of(page,size));

        model.addAttribute("listCollabs",pageUsers.getContent());
        model.addAttribute("pages",new int[pageUsers.getTotalPages()]);
        model.addAttribute("currentPage",page);
        model.addAttribute("keyword",keyword);
        return "collab";
    }
    @GetMapping(path="/admin/home")
    public  String home(Model model, @RequestParam(name="page",defaultValue = "0")int page
            , @RequestParam(name="size",defaultValue = "5")int size,
                         @RequestParam(name="keyword",defaultValue = "")String keyword)
    {
        List<User> listCollabs = userRepository.findAll();
        List<UserTaskSummaryDTO> summaries = new ArrayList<>();


        for (User user : listCollabs) {
            UserTaskSummaryDTO summary = new UserTaskSummaryDTO();
            summary.setEmail(user.getEmail());
            Long a=user.getTaches().stream().filter(t -> t.getEtatAvancement() == Etats.NON_COMMENCE).count();
            Long r=user.getTaches().stream().filter(t -> t.getEtatAvancement() == Etats.EN_ATTENTE).count();
            Long z=user.getTaches().stream().filter(t -> t.getEtatAvancement() == Etats.EN_COURS).count();
            Long b=user.getTaches().stream().filter(t -> t.getEtatAvancement() == Etats.TERMINE).count();
            System.out.print("a= "+a);
            System.out.print("  ");
            System.out.print("r= "+r);
            System.out.print("  ");
            System.out.print("z= "+z);
            System.out.print("  ");
            System.out.println("b= "+b);
            System.out.println("*********************************");
            if((r+a+z+b)==0 ){
                System.out.println("equal0");
                summary.setNonCommenceCount(0);
                summary.setEnAttenteCount(0);
                summary.setEnCoursCount(0);
                summary.setTermineCount(0);

            }else{
                long aresult=(a*100)/(r+a+b+z);
                summary.setNonCommenceCount((int)aresult);
                System.out.println("aresult = "+aresult);
                long rressult=(r*100/(r+a+z+b));
                summary.setEnAttenteCount((int)rressult);
                System.out.println("rressult = "+rressult);
                long zresult=(z*100)/(r+a+z+b);
                summary.setEnCoursCount((int)zresult);
                System.out.println("zresult = "+zresult);
                long bresult=(b*100)/(r+a+z+b);
                summary.setTermineCount((int)bresult);
                System.out.println("bresult = "+bresult);
            }
            System.out.println("*********************************");
            System.out.println(summary.getNonCommenceCount());
            System.out.println("*********************************");
            System.out.println(summary.getEnAttenteCount());
            System.out.println("*********************************");
            System.out.println(summary.getEnCoursCount());
            System.out.println("*********************************");
            System.out.println(summary.getTermineCount());


            summaries.add(summary);


        }
        List<Tache> listtache=tacheRepository.findAll();
        model.addAttribute("summaries",summaries);
        model.addAttribute("summariesCount",summaries.size());
        model.addAttribute("listtache",listtache.size());

        return "home1";
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


        userService.addRoleToUser(collab.getEmail(),roles);
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
    @PostMapping("/admin/importCollab")
    public String importCollabFromExcel(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        excelService.importCollabFromExcel(inputStream,userRepository);
        inputStream.close();

        return "redirect:/admin/collabs";
    }
}
