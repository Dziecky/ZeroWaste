package projekt.zespolowy.zero_waste.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import projekt.zespolowy.zero_waste.entity.Bid;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.repository.BidRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BidServiceTest {

    @InjectMocks
    private BidService bidService;

    @Mock
    private BidRepository bidRepository;

    @Mock
    private ProductService productService;

    private Product product;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product();
        product.setAuction(true);
        product.setAvailable(true);
        product.setEndDate(LocalDateTime.now().plusDays(1));
        product.setPrice(100.0);
        product.setOwner(new User());

        user = new User();
        user.setId(2L);
    }

    @Test
    void shouldPlaceBid_whenValid() {
        double amount = 150.0;
        when(bidRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Bid bid = bidService.placeBid(product, user, amount);

        assertEquals(amount, bid.getAmount());
        verify(productService).saveProduct(product);
        verify(bidRepository).save(any(Bid.class));
    }

    @Test
    void shouldThrow_whenBidTooLow() {
        assertThrows(IllegalStateException.class, () ->
                bidService.placeBid(product, user, 90.0));
    }

    @Test
    void shouldThrow_whenAuctionEnded() {
        product.setEndDate(LocalDateTime.now().minusDays(1));
        assertThrows(IllegalStateException.class, () ->
                bidService.placeBid(product, user, 120.0));
    }

    @Test
    void shouldThrow_whenUserBidsOnOwnAuction() {
        product.setOwner(user);
        assertThrows(IllegalStateException.class, () ->
                bidService.placeBid(product, user, 150.0));
    }

    @Test
    void shouldReturnCorrectBidCount() {
        when(bidRepository.findByProductOrderByAmountDesc(product))
                .thenReturn(List.of(new Bid(), new Bid(), new Bid()));

        int count = bidService.getBidCountForProduct(product);
        assertEquals(3, count);
    }

    @Test
    void shouldReturnHighestBidForProduct() {
        Bid highestBid = new Bid();
        highestBid.setAmount(200.0);
        when(bidRepository.findFirstByProductOrderByAmountDesc(product)).thenReturn(Optional.of(highestBid));

        Optional<Bid> result = bidService.getHighestBidForProduct(product);
        assertTrue(result.isPresent());
        assertEquals(200.0, result.get().getAmount());
    }
}
