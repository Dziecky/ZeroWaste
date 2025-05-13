package projekt.zespolowy.zero_waste.services.EducationalServices;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import projekt.zespolowy.zero_waste.entity.Diy;
import projekt.zespolowy.zero_waste.repository.DiyRepository;

import java.util.Optional;
import java.util.Arrays; // Dodano import Arrays
import java.util.List; // Dodano import List

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy; // Import do testowania wyjątków
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiyServiceTest {

    @Mock
    private DiyRepository diyRepository;

    @InjectMocks
    private DiyService diyService;


    @Test
    void testCreateDiyCallsRepositorySave() {
        Diy newDiy = new Diy(null, "bNew.jpg", "aNew.jpg", "New Desc", "New Full");
        when(diyRepository.save(newDiy)).thenReturn(newDiy);

        Diy savedDiy = diyService.createDiy(newDiy);

        assertThat(savedDiy).isEqualTo(newDiy); // Sprawdzamy, czy serwis zwrócił oczekiwany wynik
        verify(diyRepository).save(newDiy); // Sprawdzamy, czy repozytorium.save() zostało wywołane
        verifyNoMoreInteractions(diyRepository); // Sprawdzamy, czy nie było innych wywołań na mocku repozytorium
    }

    @Test
    void testGetDiyByIdWhenFound() {
        Long testId = 1L;
        Diy testDiy = new Diy(testId, "b.jpg", "a.jpg", "Test Desc", "Test Full Desc");
        when(diyRepository.findById(testId)).thenReturn(Optional.of(testDiy));

        Optional<Diy> result = diyService.getDiyById(testId);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testDiy);
        verify(diyRepository).findById(testId);
        verifyNoMoreInteractions(diyRepository);
    }

    @Test
    void testGetDiyByIdWhenNotFound() {
        Long testId = 99L;
        when(diyRepository.findById(testId)).thenReturn(Optional.empty());

        Optional<Diy> result = diyService.getDiyById(testId);

        assertThat(result).isEmpty();
        verify(diyRepository).findById(testId);
        verifyNoMoreInteractions(diyRepository);
    }

    @Test
    void testGetDiyByIdWithNullId_ShouldCallRepositoryFindByIdWithNull() {

        // Act (Wywołanie metody serwisu z null ID)
        Optional<Diy> result = diyService.getDiyById(null);


        verify(diyRepository).findById(null);
        assertThat(result).isEmpty();
        verifyNoMoreInteractions(diyRepository);
    }


    @Test
    void testDeleteDiyCallsRepositoryDeleteById() {
        Long testId = 10L;
        // W tym prostym serwisie deleteById po prostu przekazuje ID do repozytorium
        // Nie potrzebujemy mockować findById, chyba że serwis najpierw pobiera obiekt

        // Act (Wywołanie metody serwisu)
        diyService.deleteDiy(testId);

        // Assert (Sprawdzenie)
        verify(diyRepository).deleteById(testId); // Sprawdzamy, czy deleteById(10L) zostało wywołane
        verifyNoMoreInteractions(diyRepository);
    }


   }
