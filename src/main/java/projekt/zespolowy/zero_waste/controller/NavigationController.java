package projekt.zespolowy.zero_waste.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavigationController {

    @GetMapping("/info")
    public String getInfo(Model model){
        model.addAttribute("activePage", "info");
        return "info";
    }

    @GetMapping("/contact")
    public String getContact(Model model){
        model.addAttribute("activePage", "contact");
        return "contact";
    }

    @GetMapping("/company")
    public String getCompany(Model model){
        model.addAttribute("activePage", "company");
        return "company";
    }

}
