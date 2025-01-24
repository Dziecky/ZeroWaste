package projekt.zespolowy.zero_waste.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.Bid;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.repository.BidRepository;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final ProductService productService;
    @Autowired
    public BidService(BidRepository bidRepository, ProductService productService) {
        this.bidRepository = bidRepository;
        this.productService = productService;
    }

    @Transactional
    public Bid placeBid(Product product, User user, double amount) {
        validateBid(product, user, amount);

        Bid bid = new Bid();
        bid.setProduct(product);
        bid.setUser(user);
        bid.setAmount(amount);

        product.setPrice(amount);
        productService.saveProduct(product);

        return bidRepository.save(bid);
    }

    private void validateBid(Product product, User user, double amount) {
        if (!product.isAuction()) {
            throw new IllegalStateException("This product is not an auction");
        }

        if (!product.isAvailable()) {
            throw new IllegalStateException("This auction is no longer available");
        }

        if (product.getEndDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("This auction has ended");
        }

        if (product.getOwner().equals(user)) {
            throw new IllegalStateException("You can't bid on your own auction");
        }

        if (amount <= product.getPrice()) {
            throw new IllegalStateException("Bid amount must be higher than current price");
        }

    }

    public List<Bid> getBidsForProduct(Product product) {
        return bidRepository.findByProductOrderByAmountDesc(product);
    }

    public List<Bid> getBidsForUser(User user) {
        return bidRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Optional<Bid> getHighestBidForProduct(Product product) {
        return bidRepository.findFirstByProductOrderByAmountDesc(product);
    }

    public void saveBid(Bid bid) {
        bidRepository.save(bid);
    }

    public boolean hasAnyBids(Product product) {
        return !getBidsForProduct(product).isEmpty();
    }

}