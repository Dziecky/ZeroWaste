package projekt.zespolowy.zero_waste.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projekt.zespolowy.zero_waste.dto.AdviceDTO;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.Advice;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.AdviceCategory;
import projekt.zespolowy.zero_waste.mapper.AdviceMapper;
import projekt.zespolowy.zero_waste.services.AdviceService;

import java.util.Optional;

@Controller
@RequestMapping("/advices")
public class AdviceController {
    private final AdviceService adviceService;
    private final AdviceMapper adviceMapper;

    @Autowired
    public AdviceController(AdviceService adviceService, AdviceMapper adviceMapper) {
        this.adviceService = adviceService;
        this.adviceMapper = adviceMapper;
    }
    @GetMapping
    public String listAdvices(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(required = false) AdviceCategory category,
                              @RequestParam(required = false) String title,
                              Model model) {
        Page<Advice> advicePage;
        if(category != null) {
            advicePage = adviceService.getAdvicesByCategory(category, PageRequest.of(page, size));
        } else if(title != null && !title.trim().isEmpty()) {
            advicePage = adviceService.getAdvicesByTitle(title, PageRequest.of(page, size));
        } else {
            advicePage = adviceService.getAllAdvices(PageRequest.of(page, size));
        }
        model.addAttribute("advicePage", advicePage);
        model.addAttribute("activePage", "advices");
        model.addAttribute("selectedCategory", category);
        model.addAttribute("categories", AdviceCategory.values());
        model.addAttribute("title", title);
        return "Educational/Advices/advices";
    }

    // Show the form to create a new advice
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("adviceDTO", new AdviceDTO());
        model.addAttribute("categories", AdviceCategory.values());
        return "Educational/Advices/advice_form";
    }
    // Save the new advice
    @PostMapping("/save")
    public String createAdvice(@ModelAttribute("adviceDTO") AdviceDTO adviceDTO) {
        adviceService.createAdvice(adviceDTO);
        return "redirect:/advices";
    }
    // Show the form to edit an advice
    @GetMapping("/edit/{id}")
    public String showEditForm (@PathVariable("id")Long id, Model model) {
        Optional<Advice> optionalAdvice = adviceService.getAdviceById(id);
        if (optionalAdvice.isPresent()) {
            AdviceDTO adviceDTO = adviceMapper.toDTO(optionalAdvice.get());
            model.addAttribute("adviceDTO", adviceDTO);
            model.addAttribute("categories", AdviceCategory.values());
            //model.addAttribute("tags", adviceDTO.getTags());
            model.addAttribute("adviceId", id);
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
    @PostMapping("/update/{id}")
    public String updateAdvice(@PathVariable("id") Long id, @ModelAttribute("adviceDTO") AdviceDTO adviceDTO) {
        adviceService.updateAdvice(id, adviceDTO);
        return "redirect:/advices";
    }

    //View an advice
    @GetMapping("/{id}")
    public String viewAdvice(@PathVariable("id")Long id, Model model) {
        Optional<Advice> optionalAdvice = adviceService.getAdviceById(id);
        if (optionalAdvice.isPresent()) {
            AdviceDTO adviceDTO = adviceMapper.toDTO(optionalAdvice.get());
            model.addAttribute("adviceDTO", adviceDTO);
            return "Educational/Advices/advice_view";
        } else {
            return "redirect:/advices";
        }
    }

}
