package projekt.zespolowy.zero_waste.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import projekt.zespolowy.zero_waste.entity.Order;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.Refund;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.RefundStatus;
import projekt.zespolowy.zero_waste.repository.RefundRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class RefundServiceTest {

    @Mock
    private RefundRepository refundRepository;

    @InjectMocks
    private RefundService refundService;

    private User user;
    private Refund refund;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("john");

        Product product = new Product();
        product.setName("Jar");

        Order order = new Order(user, product);

        refund = new Refund();
        refund.setId(1L);
        refund.setOrder(order);
        refund.setRefundAmount(15.0);
        refund.setRefundReason("Broken");
        refund.setRequestDate(LocalDateTime.now());
        refund.setStatus(RefundStatus.APPROVED);
    }

    @Test
    void getRefundsByUser_shouldReturnPagedRefundsForUser() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "requestDate"));
        Page<Refund> page = new PageImpl<>(List.of(refund));

        when(refundRepository.findAllByOrder_User(eq(user), eq(pageable))).thenReturn(page);

        Page<Refund> result = refundService.getRefundsByUser(user, 0, 10);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getRefundReason()).isEqualTo("Broken");

        verify(refundRepository, times(1)).findAllByOrder_User(eq(user), eq(pageable));
    }

    @Test
    void getRefundsByUser_shouldReturnEmptyPageWhenUserHasNoRefunds() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "requestDate"));
        Page<Refund> emptyPage = Page.empty(pageable);

        when(refundRepository.findAllByOrder_User(eq(user), eq(pageable))).thenReturn(emptyPage);

        Page<Refund> result = refundService.getRefundsByUser(user, 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        verify(refundRepository, times(1)).findAllByOrder_User(eq(user), eq(pageable));
    }

    @Test
    void getAllRefunds_shouldReturnPagedRefundsSortedByRequestDate() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "requestDate"));
        Page<Refund> page = new PageImpl<>(List.of(refund));

        when(refundRepository.findAllWithOrderAndUser(eq(pageable))).thenReturn(page);

        Page<Refund> result = refundService.getAllRefunds(0, 10);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(refund);

        verify(refundRepository, times(1)).findAllWithOrderAndUser(eq(pageable));
    }

    @Test
    void getAllRefunds_shouldReturnEmptyPageWhenNoRefundsExist() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "requestDate"));
        Page<Refund> emptyPage = Page.empty(pageable);

        when(refundRepository.findAllWithOrderAndUser(eq(pageable))).thenReturn(emptyPage);

        Page<Refund> result = refundService.getAllRefunds(0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        verify(refundRepository, times(1)).findAllWithOrderAndUser(eq(pageable));
    }

}
