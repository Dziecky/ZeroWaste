package projekt.zespolowy.zero_waste.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.ProductCategory;
import projekt.zespolowy.zero_waste.entity.UnitOfMeasure;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.PrivacyOptions;
import projekt.zespolowy.zero_waste.services.ProductService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    private static final String VIEWED_PRODUCTS_SESSION_KEY = "viewedProducts";
    private static final int MAX_HISTORY_SIZE = 3;

    @Autowired
    public ProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping("/list")
    public String listProducts(
            @RequestParam(value = "category", required = false) ProductCategory category,
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @RequestParam(value = "sort", defaultValue = "dateDesc") String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model
    ) {
        Pageable paging = PageRequest.of(page, 10);
        Page<Product> pageProducts;
        if (category != null) {
            if ("priceAsc".equals(sort)) {
                pageProducts = productService.getProductsByCategoryAndNameContainingIgnoreCaseSortedByPriceAsc(category, search, paging);
            } else if ("priceDesc".equals(sort)) {
                pageProducts = productService.getProductsByCategoryAndNameContainingIgnoreCaseSortedByPriceDesc(category, search, paging);
            } else {
                pageProducts = productService.getProductsByCategoryAndNameContainingIgnoreCaseSortedByDateDesc(category, search, paging);
            }
        } else {
            if ("priceAsc".equals(sort)) {
                pageProducts = productService.getProductsByNameContainingIgnoreCaseSortedByPriceAsc(search, paging);
            } else if ("priceDesc".equals(sort)) {
                pageProducts = productService.getProductsByNameContainingIgnoreCaseSortedByPriceDesc(search, paging);
            } else {
                pageProducts = productService.getProductsByNameContainingIgnoreCaseSortedByDateDesc(search, paging);
            }
        }
        model.addAttribute("products", pageProducts.getContent());
        model.addAttribute("categories", ProductCategory.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("search", search);
        model.addAttribute("currentPage", pageProducts.getNumber());
        model.addAttribute("totalPages", pageProducts.getTotalPages());
        model.addAttribute("totalItems", pageProducts.getTotalElements());

        return "/product/list-products";
    }

    @GetMapping("/showFormForAddProduct")
    public String showFormForAddProduct(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        model.addAttribute("categories", ProductCategory.values());
        model.addAttribute("units", UnitOfMeasure.values());
        return "/product/product-form";
    }

    @PostMapping("/save")
    public String createProduct(@ModelAttribute("product") Product product, BindingResult bindingResult, Authentication authentication) {
        if (product.isAuction() && product.getEndDate() == null) {
            bindingResult.rejectValue("endDate", "error.product", "End date is required for auction products.");
        }
        if (bindingResult.hasErrors()) {
            return "/product/product-form";
        }
        String currentUsername = authentication.getName();
        User currentUser = userService.findByUsername(currentUsername);
        product.setOwner(currentUser);
        productService.saveProduct(product);
        return "redirect:/products/list";
    }

    @GetMapping("/edit/{id}")
    public String showFormForUpdate(@PathVariable("id") Long id, Model model, Authentication authentication) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Product ID: " + id));
        String currentUsername = authentication.getName();
        if (!product.getOwner().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You do not have permission to edit this product.");
        }
        model.addAttribute("product", product);
        model.addAttribute("categories", ProductCategory.values());
        model.addAttribute("units", UnitOfMeasure.values());
        return "/product/product-form";
    }

    @PostMapping("/update")
    public String updateProduct(@ModelAttribute("product") Product product, Authentication authentication) {
        Product existingProduct = productService.getProductById(product.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + product.getId()));

        String currentUsername = authentication.getName();
        if (!existingProduct.getOwner().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You are not authorized to edit this product.");
        }
        product.setCreatedAt(existingProduct.getCreatedAt());
        product.setOwner(existingProduct.getOwner());
        productService.saveProduct(product);
        return "redirect:/products/list";
    }


    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, Authentication authentication) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Product ID: " + id));
        String currentUsername = authentication.getName();
        if (!product.getOwner().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You do not have permission to delete this product.");
        }
        productService.deleteProduct(id);
        return "redirect:/products/list";
    }

    @GetMapping("/view/{id}")
    public String viewProductDetails(@PathVariable("id") Long id, Model model, HttpSession session, Authentication authentication) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Product ID: " + id));
        model.addAttribute("product", product);
        productService.addToViewHistory(session, id);
        model.addAttribute("recentlyViewedProducts", productService.getRecentlyViewedProductsExcept(session, id));
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByUsername(authentication.getName());
            boolean isFavorite = productService.isProductFavorite(user.getId(), id);
            model.addAttribute("isFavorite", isFavorite);
        }

        return "/product/product-detail";
    }


    @GetMapping("/recently-viewed")
    public String showRecentlyViewedProducts(Model model, HttpSession session) {
        List<Product> recentlyViewedProducts = productService.getRecentlyViewedProducts(session);
        model.addAttribute("recentlyViewedProducts", recentlyViewedProducts);
        return "/product/recently-viewed";
    }


    @PostMapping("/favorite/{id}")
    public String addProductToFavorites(@PathVariable("id") Long id, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        productService.addFavoriteProduct(user.getId(), id);
        return "redirect:/products/view/" + id;
    }

    @PostMapping("/unfavorite/{id}")
    public String removeProductFromFavorites(@PathVariable("id") Long id, Authentication authentication,
                                             @RequestHeader(value = "referer", required = false) String referer) {
        User user = userService.findByUsername(authentication.getName());
        productService.removeFavoriteProduct(user.getId(), id);
        return "redirect:" + (referer != null ? referer : "/products/favorites");
    }

    @GetMapping("/favorites")
    public String showFavoriteProducts(Model model, Authentication authentication) {
        String currentUsername = authentication.getName();
        User user = userService.findByUsername(currentUsername);
        List<Product> favoriteProducts = productService.getFavoriteProducts(user.getId());
        model.addAttribute("favoriteProducts", favoriteProducts);
        return "/product/favorite-products";
    }


}
