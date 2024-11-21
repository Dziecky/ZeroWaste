package projekt.zespolowy.zero_waste.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.Advice;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.AdviceCategory;
import projekt.zespolowy.zero_waste.services.AdviceService;

import java.util.Optional;

@Controller
@RequestMapping("/advices")
public class AdviceController {
    private final AdviceService adviceService;

    @Autowired
    public AdviceController(AdviceService adviceService) {
        this.adviceService = adviceService;
    }
    @GetMapping
    public String listAdvices(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(required = false) AdviceCategory category,
                              Model model) {
        Page<Advice> advicePage = adviceService.getAdvicesByCategory(category, PageRequest.of(page, size));
        model.addAttribute("advicePage", advicePage);
        model.addAttribute("activePage", "advices");
        model.addAttribute("selectedCategory", category);
        model.addAttribute("categories", AdviceCategory.values());
        return "Educational/Advices/advices";
    }

    // Show the form to create a new advice
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("advice", new Advice());
        model.addAttribute("categories", AdviceCategory.values());
        return "Educational/Advices/advice_form";
    }
    // Save the new advice
    @PostMapping("/save")
    public String saveAdvice(@ModelAttribute("advice") Advice advice) {
        adviceService.saveAdvice(advice);
        return "redirect:/advices";
    }
    // Show the form to edit an advice
    @GetMapping("/edit/{id}")
    public String showEditForm (@PathVariable("id")Long id, Model model) {
        Optional<Advice> optionalAdvice = adviceService.getAdviceById(id);
        if (optionalAdvice.isPresent()) {
            model.addAttribute("advice", optionalAdvice.get());
            model.addAttribute("categories", AdviceCategory.values());
            return "Educational/Advices/advice_form";
        } else {
            return "redirect:/advices";
        }
    }
    // Delete an advice
    @GetMapping("/delete/{id}") //do poprawienia na DeleteMapping
    public String deleteAdvice(@PathVariable("id")Long id) {
        adviceService.deleteAdvice(id);
        return "redirect:/advices";
    }

    //View an advice
    @GetMapping("/{id}")
    public String viewAdvice(@PathVariable("id")Long id, Model model) {
        Optional<Advice> optionalAdvice = adviceService.getAdviceById(id);
        if (optionalAdvice.isPresent()) {
            model.addAttribute("advice", optionalAdvice.get());
            return "Educational/Advices/advice_view";
        } else {
            return "redirect:/advices";
        }
    }

}
