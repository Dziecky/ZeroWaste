package projekt.zespolowy.zero_waste.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import projekt.zespolowy.zero_waste.dto.user.ActivityLogDto;
import projekt.zespolowy.zero_waste.entity.ActivityLog;
import projekt.zespolowy.zero_waste.entity.enums.ActivityType;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.repository.ActivityLogRepository;
import projekt.zespolowy.zero_waste.services.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ActivityControllerTest {

    @Mock
    private ActivityLogRepository logRepository;

    @InjectMocks
    private ActivityController controller;

    private MockMvc mockMvc;
    private MockedStatic<UserService> userServiceStatic;

    private final User testUser = new User();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        // Prepare a test user
        testUser.setId(42L);

        // Mock static UserService.getUser()
        userServiceStatic = Mockito.mockStatic(UserService.class);
        userServiceStatic.when(UserService::getUser).thenReturn(testUser);
    }

    @AfterEach
    void tearDown() {
        if (userServiceStatic != null) {
            userServiceStatic.close();
        }
    }

    @Test
    void whenNoFilter_thenFetchByUserId() throws Exception {
        // Given
        ActivityLog log1 = new ActivityLog();
        log1.setId(1L);
        log1.setActivityType(ActivityType.ITEM_ADDED);
        log1.setOccurredAt(LocalDateTime.now());

        List<ActivityLog> logs = List.of(log1);
        Page<ActivityLog> page = new PageImpl<>(logs, PageRequest.of(0, 10, Sort.by("occurredAt").descending()), logs.size());
        given(logRepository.findByUserId(eq(42L), any(Pageable.class))).willReturn(page);

        // When & Then
        mockMvc.perform(get("/me/activity").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("activity/history"))
                .andExpect(model().attributeExists("logsPage"))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(model().attribute("size", 10))
                .andExpect(model().attribute("filter", nullValue()))
                .andExpect(model().attribute("activityTypes", ActivityType.values()))
                .andExpect(model().attribute("baseUrl", "/me/activity"));
    }

    @Test
    void whenFilter_thenFetchByUserIdAndType() throws Exception {
        // Given
        ActivityLog log2 = new ActivityLog();
        log2.setId(2L);
        log2.setActivityType(ActivityType.AUCTION_BID);
        log2.setOccurredAt(LocalDateTime.now());

        List<ActivityLog> logs = List.of(log2);
        Page<ActivityLog> page = new PageImpl<>(logs, PageRequest.of(1, 5, Sort.by("occurredAt").descending()), 12);
        given(logRepository.findByUserIdAndActivityType(eq(42L), eq(ActivityType.AUCTION_BID), any(Pageable.class))).willReturn(page);

        // When & Then
        mockMvc.perform(get("/me/activity")
                        .param("page", "1")
                        .param("size", "5")
                        .param("filter", "AUCTION_BID"))
                .andExpect(status().isOk())
                .andExpect(view().name("activity/history"))
                .andExpect(model().attributeExists("logsPage"))
                .andExpect(model().attribute("currentPage", 2))
                .andExpect(model().attribute("totalPages", 3))
                .andExpect(model().attribute("size", 5))
                .andExpect(model().attribute("filter", ActivityType.AUCTION_BID))
                .andExpect(model().attribute("activityTypes", ActivityType.values()))
                .andExpect(model().attribute("baseUrl", "/me/activity"));
    }
}
