package projekt.zespolowy.zero_waste.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projekt.zespolowy.zero_waste.dto.AdviceDTO;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.Advice;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.AdviceCategory;
import projekt.zespolowy.zero_waste.mapper.AdviceMapper;
import projekt.zespolowy.zero_waste.services.EducationalServices.Advice.AdviceService;

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
                              @RequestParam(required = false) String tagName,
                              Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Advice> advicePage = adviceService.findAdvices(category, title, tagName, pageable);

        Page<AdviceDTO> adviceDTOPage = advicePage.map(adviceMapper::toDTO);
        model.addAttribute("advicePage", adviceDTOPage);
        //model.addAttribute("activePage", "advices");
        model.addAttribute("selectedCategory", category);
        model.addAttribute("categories", AdviceCategory.values());
        model.addAttribute("selectedTagName", tagName);
        model.addAttribute("title", title);

        return "Educational/Advices/advices";
    }


    // Show the form to create a new advice
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("adviceDTO", new AdviceDTO());
        model.addAttribute("categories", AdviceCategory.values());
        return "Educational/Advices/advice_form";
    }
    // Save the new advice
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/save")
    public String createAdvice(@ModelAttribute("adviceDTO") AdviceDTO adviceDTO) {
        adviceService.createAdvice(adviceDTO);
        return "redirect:/advices";
    }
    // Show the form to edit an advice
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/edit/{id}")
    public String showEditForm (@PathVariable("id")Long id, Model model) {
        Optional<Advice> optionalAdvice = adviceService.getAdviceById(id);
        if (optionalAdvice.isPresent()) {
            AdviceDTO adviceDTO = adviceMapper.toDTO(optionalAdvice.get());
            model.addAttribute("adviceDTO", adviceDTO);
            model.addAttribute("categories", AdviceCategory.values());
            return "Educational/Advices/advice_form";
        } else {
            return "redirect:/advices";
        }
    }
    // Delete an advice
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}") //do poprawienia na DeleteMapping
    public String deleteAdvice(@PathVariable("id")Long id) {
        adviceService.deleteAdvice(id);
        return "redirect:/advices";
    }

    @PreAuthorize("isAuthenticated()")
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
