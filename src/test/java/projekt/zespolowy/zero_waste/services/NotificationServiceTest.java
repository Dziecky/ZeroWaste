package projekt.zespolowy.zero_waste.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Challenge;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Tip;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.UserPreference;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.Frequency;
import projekt.zespolowy.zero_waste.entity.enums.SubscriptionType;
import projekt.zespolowy.zero_waste.repository.ChallengeRepository;
import projekt.zespolowy.zero_waste.repository.TipRepository;
import projekt.zespolowy.zero_waste.services.EducationalServices.UserPreferenceService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    UserService userService;

    @Mock
    UserPreferenceService userPreferenceService;

    @Mock
    TipRepository tipRepository;

    @Mock
    ChallengeRepository challengeRepository;

    @InjectMocks
    NotificationService notificationService;

    @Test
    void shouldSendDailyTipWhenUserSubscribed() {
        // given
        User user = new User();
        user.setId(1L);
        user.setTips(new ArrayList<>());

        UserPreference preference = new UserPreference();
        preference.setFrequency(Frequency.DAILY);
        preference.setSubscribedTo(Set.of(SubscriptionType.TIP));

        Tip sampleTip = new Tip();
        sampleTip.setId(1L);
        sampleTip.setContent("Sample Tip");

        when(userService.getAllUsers()).thenReturn(List.of(user));
        when(userPreferenceService.getUserPreference(user)).thenReturn(Optional.of(preference));
        when(tipRepository.findRandomTip()).thenReturn(sampleTip);

        // when
        notificationService.sendNotification();

        // then
        verify(userService).save(user);
        assertTrue(user.getTips().contains(sampleTip));
    }

    @Test
    void shouldSendWeeklyChallengeWhenUserSubscribed() {
        // given
        User user = new User();
        user.setId(1L);
        user.setChallenges(new ArrayList<>());

        UserPreference preference = new UserPreference();
        preference.setFrequency(Frequency.WEEKLY);
        preference.setSubscribedTo(Set.of(SubscriptionType.CHALLENGE));

        Challenge sampleChallenge = new Challenge();
        sampleChallenge.setId(1L);
        sampleChallenge.setTitle("Weekly Challenge");

        when(userService.getAllUsers()).thenReturn(List.of(user));
        when(userPreferenceService.getUserPreference(user)).thenReturn(Optional.of(preference));
        when(challengeRepository.findRandomChallenge()).thenReturn(sampleChallenge);

        NotificationService serviceSpy = spy(notificationService);
        doReturn(LocalDate.of(2025, 3, 10)).when(serviceSpy).getCurrentDate(); // Ustawienie poniedziałku

        // when
        serviceSpy.sendNotification();

        // then
        verify(userService).save(user);
        assertTrue(user.getChallenges().contains(sampleChallenge));
    }


    @Test
    void shouldNotSendTipWhenUserNotSubscribed() {
        // given
        User user = new User();
        user.setId(3L);
        user.setTips(new ArrayList<>());

        UserPreference preference = new UserPreference();
        preference.setFrequency(Frequency.DAILY);
        preference.setSubscribedTo(Set.of());

        when(userService.getAllUsers()).thenReturn(List.of(user));
        when(userPreferenceService.getUserPreference(user)).thenReturn(Optional.of(preference));

        // when
        notificationService.sendNotification();

        // then
        verify(userService, never()).save(user);
        assertTrue(user.getTips().isEmpty());
    }

    @Test
    void shouldHandleUserWithNoPreferencesGracefully() {
        // given
        User user = new User();
        user.setId(4L);
        user.setTips(new ArrayList<>());
        user.setChallenges(new ArrayList<>());

        when(userService.getAllUsers()).thenReturn(List.of(user));
        when(userPreferenceService.getUserPreference(user)).thenReturn(Optional.empty());

        // when
        notificationService.sendNotification();

        // then
        verify(userService, never()).save(user);
        assertTrue(user.getTips().isEmpty());
        assertTrue(user.getChallenges().isEmpty());
    }
}
