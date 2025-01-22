package projekt.zespolowy.zero_waste.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projekt.zespolowy.zero_waste.entity.*;
import projekt.zespolowy.zero_waste.services.ProductService;
import projekt.zespolowy.zero_waste.services.BidService;


import java.time.LocalDateTime;
import java.util.List;

import static projekt.zespolowy.zero_waste.controller.UserController.userService;

@Controller
@RequestMapping("/auctions")
public class AuctionController {

    private final ProductService productService;
    private final BidService bidService;


    @Autowired
    public AuctionController(ProductService productService, BidService bidService) {
        this.productService = productService;
        this.bidService = bidService;
    }

    @GetMapping("/new")
    public String showNewAuctionForm(Model model) {
        Product product = new Product();
        product.setAuction(true);
        product.setAvailable(true);
        model.addAttribute("product", product);
        model.addAttribute("categories", ProductCategory.values());
        model.addAttribute("units", UnitOfMeasure.values());

        return "product/auction-form";
    }

    @PostMapping("/save")
    public String saveAuction(@ModelAttribute("product") Product product,
                              Authentication authentication,
                              Model model) {
        if (product.getEndDate() == null || product.getProductCategory() == null) {
            model.addAttribute("categories", ProductCategory.values());
            model.addAttribute("units", UnitOfMeasure.values());
            model.addAttribute("error", "Please fill all required fields");
            return "product/auction-form";
        }
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        product.setOwner(user);
        product.setAuction(true);
        product.setCreatedAt(LocalDateTime.now());

        productService.saveProduct(product);
        return "redirect:/auctions/list";
    }

    @GetMapping("/list")
    public String listAuctions(Model model) {
        List<Product> auctions = productService.getAuctionProducts();
        model.addAttribute("products", auctions);
        return "product/auction-list";
    }

    @PostMapping("/bids/add")
    public String placeBid(@RequestParam Long productId, @RequestParam double amount, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Product ID"));

        if (!product.isAuction() || product.getEndDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Bidding is not allowed for this product.");
        }

        if (amount <= product.getPrice()) {
            throw new IllegalArgumentException("Bid must be higher than current price.");
        }

        Bid bid = new Bid();
        bid.setProduct(product);
        bid.setUser(user);
        bid.setAmount(amount);
        bidService.saveBid(bid);

        product.setPrice(amount);
        productService.saveProduct(product);

        return "redirect:product/list";
    }

    @GetMapping("/{id}")
    public String showAuctionDetails(@PathVariable Long id, Model model, Authentication authentication) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid auction ID"));

        List<Bid> bids = bidService.getBidsForProduct(product);

        model.addAttribute("product", product);
        model.addAttribute("bids", bids);

        if (authentication != null) {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            model.addAttribute("userBids", bidService.getBidsForUser(user));
        }

        return "product/auction_details";
    }

    @PostMapping("/bid")
    public String placeBid(@RequestParam Long productId,
                           @RequestParam double amount,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            Product product = productService.getProductById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Product ID"));

            bidService.placeBid(product, user, amount);
            redirectAttributes.addFlashAttribute("successMessage", "Bid placed successfully!");

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while placing your bid");
        }

        return "redirect:/auctions/" + productId;
    }
}

