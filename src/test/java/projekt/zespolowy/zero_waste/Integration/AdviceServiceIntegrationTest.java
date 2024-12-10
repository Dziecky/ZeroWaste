package projekt.zespolowy.zero_waste.Integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.Advice;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.AdviceCategory;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.Article;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.ArticleCategory;
import projekt.zespolowy.zero_waste.repository.AdviceRepository;
import projekt.zespolowy.zero_waste.services.EducationalServices.Advice.AdviceService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class AdviceServiceIntegrationTest {

    @Autowired
    private AdviceService AdviceService;

    @Autowired
    private AdviceRepository AdviceRepository;
    @Test
    void testGetAdvicesByTitle_WithValidTitle() {
        // Arrange
        String title = "Integration Testing";
        Pageable pageable = PageRequest.of(0, 10);
        Advice Advice = new Advice();
        Advice.setTitle("Integration Testing in Spring Boot");
        Advice.setContent("Content about integration testing.");
        Advice.setAdviceCategory(AdviceCategory.OTHER);
        AdviceRepository.save(Advice);

        // Act
        Page<Advice> result = AdviceService.getAdvicesByTitle(title, pageable);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.getTotalElements(), "Should return one Advice");
        assertEquals("Integration Testing in Spring Boot", result.getContent().get(0).getTitle(), "Advice title should match");
    }
}

