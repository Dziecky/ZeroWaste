package projekt.zespolowy.zero_waste.services;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.Advice;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.AdviceCategory;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Articles.ArticleCategory;
import projekt.zespolowy.zero_waste.repository.AdviceRepository;
import projekt.zespolowy.zero_waste.services.EducationalServices.Advice.AdviceServiceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

public class AdviceServiceImplTest {

    @InjectMocks
    private AdviceServiceImpl adviceService;

    @Mock
    private AdviceRepository adviceRepository;

    public AdviceServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAdvicesByTitle_WithValidTitle() {
        // Arrange
        String title = "Spring Boot Testing";
        Pageable pageable = PageRequest.of(0, 10);
        Advice advice = new Advice();
        advice.setId(1L);
        advice.setTitle(title);
        advice.setContent("Content about Spring Boot Testing.");
        advice.setAdviceCategory(AdviceCategory.OTHER);

        List<Advice> advices = Collections.singletonList(advice);
        Page<Advice> advicePage = new PageImpl<>(advices, pageable, advices.size());

        when(adviceRepository.findByTitleContainingIgnoreCase(title, pageable)).thenReturn(advicePage);

        // Act
        Page<Advice> result = adviceService.getAdvicesByTitle(title, pageable);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.getTotalElements(), "Should return one Advice");
        assertEquals(title, result.getContent().get(0).getTitle(), "Advice title should match");
        verify(adviceRepository, times(1)).findByTitleContainingIgnoreCase(title, pageable);
    }

    @Test
    void testGetAdvicesByTitle_WithNoMatchingAdvices() {
        // Arrange
        String title = "Nonexistent Title";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Advice> advicePage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(adviceRepository.findByTitleContainingIgnoreCase(title, pageable)).thenReturn(advicePage);

        // Act
        Page<Advice> result = adviceService.getAdvicesByTitle(title, pageable);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(0, result.getTotalElements(), "Should return zero Advices");
        verify(adviceRepository, times(1)).findByTitleContainingIgnoreCase(title, pageable);
    }

    @Test
    void testGetAdvicesByTitle_WithNullTitle() {
        // Arrange
        String title = null;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Advice> advicePage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(adviceRepository.findAll(pageable)).thenReturn(advicePage);

        // Act
        Page<Advice> result = adviceService.getAdvicesByTitle(title, pageable);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(0, result.getTotalElements(), "Should return zero Advices");
        verify(adviceRepository, times(1)).findAll(pageable);
        verify(adviceRepository, never()).findByTitleContainingIgnoreCase(anyString(), any(Pageable.class));
    }

    @Test
    void testGetAdvicesByTitle_WithEmptyTitle() {
        // Arrange
        String title = "   "; // Title with only spaces
        Pageable pageable = PageRequest.of(0, 10);
        Page<Advice> advicePage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(adviceRepository.findAll(pageable)).thenReturn(advicePage);

        // Act
        Page<Advice> result = adviceService.getAdvicesByTitle(title, pageable);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(0, result.getTotalElements(), "Should return zero Advices");
        verify(adviceRepository, times(1)).findAll(pageable);
        verify(adviceRepository, never()).findByTitleContainingIgnoreCase(anyString(), any(Pageable.class));
    }
}

