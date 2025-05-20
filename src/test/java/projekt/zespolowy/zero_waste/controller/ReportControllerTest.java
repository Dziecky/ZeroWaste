package projekt.zespolowy.zero_waste.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import projekt.zespolowy.zero_waste.entity.Report;
import projekt.zespolowy.zero_waste.services.ReportService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReportControllerTest {

    @InjectMocks
    private AdminController controller;

    @Mock
    private ReportService reportService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // potrzebne do zwracania nazw widoków jako String
        InternalResourceViewResolver vr = new InternalResourceViewResolver();
        vr.setPrefix("/WEB-INF/views/");
        vr.setSuffix(".jsp");
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(vr)
                .build();
    }

    @Test
    void showReports_defaultNew() throws Exception {
        // przygotuj stronę z jednym raportem
        Report r = new Report();
        Page<Report> page = new PageImpl<>(List.of(r), PageRequest.of(0, 10), 1);
        when(reportService.getNewReports(0, 10)).thenReturn(page);

        mockMvc.perform(get("/admin/reports"))
                .andExpect(status().isOk())
                .andExpect(view().name("User/admin/admin-reports"))
                .andExpect(model().attribute("reports", page.getContent()))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(model().attribute("size", 10))
                .andExpect(model().attribute("baseUrl", "/admin/reports"))
                .andExpect(model().attribute("status", "NEW"));

        verify(reportService).getNewReports(0, 10);
    }

    @Test
    void showReports_resolved() throws Exception {
        Report r = new Report();
        Page<Report> page = new PageImpl<>(List.of(r), PageRequest.of(1, 5), 2);
        when(reportService.getResolvedReports(1, 5)).thenReturn(page);

        mockMvc.perform(get("/admin/reports")
                        .param("page", "2")
                        .param("size", "5")
                        .param("status", "resolved"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentPage", 2))
                .andExpect(model().attribute("totalPages", 2))
                .andExpect(model().attribute("size", 5))
                .andExpect(model().attribute("status", "RESOLVED"));

        verify(reportService).getResolvedReports(1, 5);
    }

    @Test
    void resolveReport_blockUser_success() throws Exception {
        mockMvc.perform(post("/admin/reports/resolve")
                        .param("reportId", "42")
                        .param("action", "blockUser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/reports"))
                .andExpect(flash().attribute("success", "Użytkownik został zablokowany."));

        verify(reportService).blockUser(42L);
    }

    @Test
    void resolveReport_invalidAction_noServiceCall() throws Exception {
        mockMvc.perform(post("/admin/reports/resolve")
                        .param("reportId", "1")
                        .param("action", "unknown"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/reports"))
                .andExpect(flash().attributeCount(0));

        verifyNoInteractions(reportService);
    }

    @Test
    void resolveReport_serviceThrows_exceptionMessage() throws Exception {
        doThrow(new IllegalArgumentException("Brak zgłoszenia"))
                .when(reportService).blockUser(5L);

        mockMvc.perform(post("/admin/reports/resolve")
                        .param("reportId", "5")
                        .param("action", "blockUser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/reports"))
                .andExpect(flash().attribute("error", "Brak zgłoszenia"));

        verify(reportService).blockUser(5L);
    }
}
