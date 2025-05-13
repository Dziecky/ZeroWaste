package projekt.zespolowy.zero_waste.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import projekt.zespolowy.zero_waste.entity.Diy;
import projekt.zespolowy.zero_waste.services.EducationalServices.DiyService;

import java.util.Optional;

@Controller
@RequestMapping("/diy")
public class DiyController {

    private final DiyService diyService;

    @Autowired
    public DiyController(DiyService diyService) {
        this.diyService = diyService;
    }


    @GetMapping
    public String showDiyGallery(Model model) {
        model.addAttribute("diyProjects", diyService.getAllDiys());

        return "diy";
    }

    @GetMapping("/details/{id}")
    public String showDiyDetails(@PathVariable Long id, Model model) {
        // Pobierz pojedynczy projekt po ID z serwisu
        Optional<Diy> diyOptional = diyService.getDiyById(id);

        if (diyOptional.isPresent()) {
            model.addAttribute("diyProject", diyOptional.get());
            return "diy/details";
        } else {

            return "redirect:/diy";
        }
    }


    // Wyświetla listę wszystkich projektów DIY do zarządzania (BEZ ZMIAN)
    @GetMapping("/manage")
    public String listDiy(Model model) {
        model.addAttribute("diyProjects", diyService.getAllDiys());
        return "diy/list"; // Szablon listy zarządzania (diy/list.html)
    }

    // Wyświetla formularz dodawania nowego projektu DIY (BEZ ZMIAN W SYGNATURZE)
    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("diy", new Diy()); // Przekaż pusty obiekt Diy do formularza
        return "diy/new"; // Szablon formularza dodawania (diy/new.html)
    }

    // Obsługuje wysłanie formularza dodawania (POST) - Przyjmuje obiekt Diy bindowany z formularza
    @PostMapping("/new")
    public String addDiy(Diy diy) { // Spring automatycznie zbierze pola formularza o pasujących nazwach do obiektu Diy
        // Obiekt 'diy' powinien mieć wypełnione pola description, fullDescription, beforeImageUrl, afterImageUrl
        diyService.createDiy(diy);
        // Po dodaniu, przekieruj na listę zarządzania
        return "redirect:/diy/manage";
    }


    // Wyświetla formularz edycji dla istniejącego projektu DIY (BEZ ZMIAN W SYGNATURZE)
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Diy> diyOptional = diyService.getDiyById(id);
        if (diyOptional.isPresent()) {
            model.addAttribute("diy", diyOptional.get()); // Przekaż istniejący obiekt do formularza
            return "diy/edit"; // Szablon formularza edycji (diy/edit.html)
        } else {
            return "redirect:/diy/manage"; // Jeśli nie znaleziono, wróć do listy zarządzania
        }
    }

    // Obsługuje wysłanie formularza edycji (POST) - Przyjmuje zaktualizowany obiekt Diy
    @PostMapping("/edit/{id}")
    public String updateDiy(@PathVariable Long id, Diy diy) { // Spring zbierze pola formularza do obiektu diy
        // Obiekt 'diy' będzie miał wypełnione z formularza pola description, fullDescription, beforeImageUrl, afterImageUrl,
        // a także ID (jeśli jest ukryte pole w formularzu lub PathVariable).
        // W tym przypadku PathVariable {id} jest lepsze do identyfikacji obiektu
        diyService.updateDiy(id, diy); // Przekaż ID i obiekt z danymi z formularza
        // Po edycji, przekieruj na listę zarządzania
        return "redirect:/diy/manage";
    }

    // Obsługuje żądanie usunięcia projektu DIY (BEZ ZMIAN)
    @PostMapping("/delete/{id}")
    public String deleteDiy(@PathVariable Long id) {
        diyService.deleteDiy(id);
        return "redirect:/diy/manage";
    }

}
