package projekt.zespolowy.zero_waste.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import projekt.zespolowy.zero_waste.entity.FAQ;

import java.util.Arrays;
import java.util.List;

@Controller
public class FAQController {

    @GetMapping("/faq")
    public String showFAQ(Model model) {
        List<FAQ> faqList = Arrays.asList(
                new FAQ("Czym jest Zero Waste?", "Witaj w naszej aplikacji! Jesteśmy tu, by pomóc Ci dbać o naszą planetę na wiele sposobów. Dzięki naszej platformie możesz sprzedać lub kupić używane przedmioty, znaleźć ciekawe artykuły i porady, a także śledzić statystyki związane ze stanem środowiska. To tylko niektóre z funkcji, które sprawią, że każdy Twój krok będzie miał pozytywny wpływ na przyszłość naszej planety. Razem możemy stworzyć lepsze jutro!"),
                new FAQ("Jak mogę wziąć udział?", "Zarejestruj się w aplikacji i zacznij monitorować swoje działania."),
                new FAQ("Czy mogę zmienić swoje dane?", "Tak, możesz edytować swoje dane w ustawieniach konta."),
                new FAQ("Czy aplikacja jest darmowa?", "Tak, aplikacja jest w pełni darmowa do pobrania i użytkowania.")
        );

        model.addAttribute("faqList", faqList);
        return "faq";
    }
}

