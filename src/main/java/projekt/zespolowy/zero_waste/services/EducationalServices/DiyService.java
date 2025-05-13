package projekt.zespolowy.zero_waste.services.EducationalServices;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.dto.ArticleDTO;
import projekt.zespolowy.zero_waste.entity.Diy;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.Article;
import projekt.zespolowy.zero_waste.repository.DiyRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DiyService {
    private final DiyRepository diyRepository;

    @Autowired
    public DiyService(DiyRepository diyRepository) {
        this.diyRepository = diyRepository;
    }

    @Transactional
    public Diy createDiy(Diy diy) {
        return diyRepository.save(diy);
    }

    public List<Diy> getAllDiys() {
        return diyRepository.findAll();
    }

    // Metoda aktualizacji DIY - przyjmuje zaktualizowany obiekt
    @Transactional
    public Diy updateDiy(Long id, Diy updatedDiy) {
        return diyRepository.findById(id).map(existingDiy -> {
            // Aktualizuj wszystkie pola tekstowe i URL-e obrazków
            existingDiy.setDescription(updatedDiy.getDescription());
            existingDiy.setFullDescription(updatedDiy.getFullDescription());
            existingDiy.setBeforeImageUrl(updatedDiy.getBeforeImageUrl()); // Aktualizuj URL
            existingDiy.setAfterImageUrl(updatedDiy.getAfterImageUrl());   // Aktualizuj URL

            return diyRepository.save(existingDiy); // Zapisz zaktualizowaną encję
        }).orElseThrow(() -> new RuntimeException("Diy not found with id " + id));
    }
    public Optional<Diy> getDiyById(Long id) {
        return diyRepository.findById(id);
    }
    public void deleteDiy(Long id) {
        diyRepository.deleteById(id);
    }

}
