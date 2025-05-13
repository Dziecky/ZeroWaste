package projekt.zespolowy.zero_waste.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.assertj.core.data.TemporalUnitWithinOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import projekt.zespolowy.zero_waste.entity.ActivityLog;
import projekt.zespolowy.zero_waste.entity.enums.ActivityType;
import projekt.zespolowy.zero_waste.repository.ActivityLogRepository;
import projekt.zespolowy.zero_waste.services.ActivityLogService;

class ActivityLogServiceTest {

    @Mock
    private ActivityLogRepository logRepository;

    @InjectMocks
    private ActivityLogService service;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void log_shouldSaveNewEntry() {
        // given
        Long userId = 99L;
        ActivityType type = ActivityType.AUCTION_BID;
        Long entityId = 123L;
        Map<String,Object> details = Map.of("bid", 50);

        // when
        service.log(userId, type, entityId, details);

        // then
        ArgumentCaptor<ActivityLog> captor = ArgumentCaptor.forClass(ActivityLog.class);
        verify(logRepository).save(captor.capture());
        ActivityLog saved = captor.getValue();

        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getActivityType()).isEqualTo(type);
        assertThat(saved.getEntityId()).isEqualTo(entityId);
        assertThat(saved.getDetails()).isEqualTo(details);
        // timestamp powinien być blisko teraz (w granicach 1 sekundy)
        assertThat(saved.getOccurredAt())
                .isCloseTo(LocalDateTime.now(),
                        within(1, ChronoUnit.SECONDS));
    }
}
