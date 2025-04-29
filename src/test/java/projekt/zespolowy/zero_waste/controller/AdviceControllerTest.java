package projekt.zespolowy.zero_waste.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import projekt.zespolowy.zero_waste.dto.AdviceDTO;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.Advice;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.AdviceCategory;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.mapper.AdviceMapper;
import projekt.zespolowy.zero_waste.services.EducationalServices.Advice.AdviceService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdviceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AdviceService adviceService;

    @Mock
    private AdviceMapper adviceMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private AdviceController adviceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adviceController).build();
    }

    @Test
    void showCreateForm_shouldReturnAdviceForm() throws Exception {
        mockMvc.perform(get("/advices/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("Educational/Advices/advice_form"))
                .andExpect(model().attributeExists("adviceDTO"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void createAdvice_shouldRedirectToList() throws Exception {
        mockMvc.perform(post("/advices/save")
                        .param("title", "Test Title")
                        .param("content", "Test Content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/advices"));

        verify(adviceService).createAdvice(any(AdviceDTO.class));
    }

    @Test
    void showEditForm_shouldReturnFormWhenAdviceExists() throws Exception {
        // Arrange
        Advice advice = new Advice();
        when(adviceService.getAdviceById(anyLong())).thenReturn(Optional.of(advice));
        when(adviceMapper.toDTO(any(Advice.class))).thenReturn(new AdviceDTO());

        // Act & Assert
        mockMvc.perform(get("/advices/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("Educational/Advices/advice_form"))
                .andExpect(model().attributeExists("adviceDTO"));
    }

    @Test
    void showEditForm_shouldRedirectWhenAdviceNotFound() throws Exception {
        when(adviceService.getAdviceById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/advices/edit/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/advices"));
    }

    @Test
    void deleteAdvice_shouldRedirectToList() throws Exception {
        mockMvc.perform(get("/advices/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/advices"));

        verify(adviceService).deleteAdvice(1L);
    }

    @Test
    void likeAdvice_shouldRedirectBack() throws Exception {
        mockMvc.perform(post("/advices/like/1")
                        .header("Referer", "/previous-page"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/previous-page"));

        verify(adviceService).toggleLikeAdvice(1L);
    }
}