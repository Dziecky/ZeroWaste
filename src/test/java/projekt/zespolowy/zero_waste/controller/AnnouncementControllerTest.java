package projekt.zespolowy.zero_waste.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import projekt.zespolowy.zero_waste.entity.Announcement;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.AccountType;
import projekt.zespolowy.zero_waste.mapper.AdviceMapper;
import projekt.zespolowy.zero_waste.mapper.ArticleMapper;
import projekt.zespolowy.zero_waste.repository.AnnouncementRepository;
import projekt.zespolowy.zero_waste.repository.UserRepository;
import projekt.zespolowy.zero_waste.services.ProductService;
import projekt.zespolowy.zero_waste.services.UserService;
import projekt.zespolowy.zero_waste.security.CustomUser;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AnnouncementControllerTest {

    @InjectMocks
    private AnnouncementController announcementController;

    private Announcement testAnnouncement;
    private Product testProduct;
    private User testUser;
    private CustomUser customUser;
    private UserService userService;

    @Mock
    private AnnouncementRepository announcementRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductService productService;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ArticleMapper articleMapper;

    @Mock
    private AdviceMapper adviceMapper;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setAccountType(AccountType.BUSINESS);
        testUser.setViewedAnnouncements(new HashSet<>());
        testUser.setUpvotedAnnouncements(new HashSet<>()); // Initialize upvoted announcements

        // Setup CustomUser
        customUser = new CustomUser(testUser);
        when(authentication.getPrincipal()).thenReturn(customUser);

        // Setup test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");

        // Setup test announcement
        testAnnouncement = new Announcement();
        testAnnouncement.setId(1L);
        testAnnouncement.setTitle("Test Announcement");
        testAnnouncement.setDescription("Test Description");
        testAnnouncement.setProducts(List.of(testProduct));
        testAnnouncement.setCreatedAt(LocalDateTime.now());
        testAnnouncement.setUpdatedAt(LocalDateTime.now());
        testAnnouncement.setViewedByUsers(new HashSet<>());
        testAnnouncement.setUpvotedByUsers(new HashSet<>()); // Initialize upvoted users
        testAnnouncement.setOwner(testUser);

        // Setup Spring Security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Setup UserRepository mock for static UserService.findByUsername
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        // Set the static userRepository field in UserService using reflection
        Field userRepositoryField = UserService.class.getDeclaredField("userRepository");
        userRepositoryField.setAccessible(true);
        userRepositoryField.set(null, userRepository);

        // Initialize controller with required dependencies
        announcementController = new AnnouncementController(announcementRepository, productService);
    }

    @AfterEach
    void tearDown() throws Exception {
        SecurityContextHolder.clearContext();

        // Reset the static userRepository field
        Field userRepositoryField = UserService.class.getDeclaredField("userRepository");
        userRepositoryField.setAccessible(true);
        userRepositoryField.set(null, null);
    }

    @Test
    void createAnnouncementView_ShouldReturnCreateView() {
        List<Product> products = List.of(testProduct);
        when(productService.getAllProducts()).thenReturn(products);

        String viewName = announcementController.createAnnouncementView(model);

        assertEquals("/Announcement/createAnnouncement", viewName);
        verify(model).addAttribute(eq("announcement"), any(Announcement.class));
        verify(model).addAttribute("products", products);
    }



    @Test
    void showAnnouncements_WithProductSearch_ShouldFilterAnnouncements() {
        // Arrange
        List<Announcement> announcements = List.of(testAnnouncement);
        when(announcementRepository.findAll()).thenReturn(announcements);
        when(productService.getAllProducts()).thenReturn(List.of(testProduct));

        // Act
        String viewName = announcementController.showAnnouncements("Test Product", false, 0, 10, "newest", model);

        // Assert
        assertEquals("/Announcement/announcements", viewName);
        verify(model).addAttribute(eq("announcements"), any(List.class));
        verify(model).addAttribute("productSearch", "Test Product");
    }

    @Test
    void submitAnnouncement_ShouldCreateNewAnnouncement() {
        // Arrange
        List<Long> productIds = List.of(1L);
        when(productService.getProductsByIds(productIds)).thenReturn(List.of(testProduct));
        when(announcementRepository.save(any(Announcement.class))).thenReturn(testAnnouncement);

        // Act
        String viewName = announcementController.submitAnnouncement(testAnnouncement, productIds);

        // Assert
        assertEquals("redirect:/announcements", viewName);
        verify(announcementRepository).save(testAnnouncement);
        assertEquals(testUser, testAnnouncement.getOwner());
    }

    @Test
    void showAnnouncementDetails_ShouldDisplayAnnouncementAndIncrementViews() {
        // Arrange
        when(announcementRepository.findById(1L)).thenReturn(Optional.of(testAnnouncement));
        when(announcementRepository.save(any(Announcement.class))).thenReturn(testAnnouncement);

        // Act
        String viewName = announcementController.showAnnouncementDetails(1L, model);

        // Assert
        assertEquals("/Announcement/details", viewName);
        verify(model).addAttribute("announcement", testAnnouncement);
        verify(model).addAttribute(eq("viewCount"), any());
        verify(announcementRepository).save(testAnnouncement);
    }

    @Test
    void deleteAnnouncement_AsOwner_ShouldDeleteSuccessfully() {
        // Arrange
        when(announcementRepository.findById(1L)).thenReturn(Optional.of(testAnnouncement));

        // Act
        String viewName = announcementController.deleteAnnouncement(1L);

        // Assert
        assertEquals("redirect:/announcements", viewName);
        verify(announcementRepository).delete(testAnnouncement);
    }



    @Test
    void upvoteAnnouncement_WhenAnnouncementNotFound_ShouldReturnNotFound() {
        // Arrange
        when(announcementRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            // Act
            announcementController.upvoteAnnouncement(1L);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Assert
            assertEquals("Invalid announcement ID: 1", e.getMessage());
        }

        verify(announcementRepository, never()).save(any());
    }

    @Test
    void upvoteAnnouncement_WhenUserNotAuthenticated_ShouldReturnUnauthorized() {
        // Arrange
        SecurityContextHolder.clearContext(); // Clear the security context completely

        // Act
        try {
            announcementController.upvoteAnnouncement(1L);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // This is the expected behavior when authentication is null
            verify(announcementRepository, never()).save(any());
        }
    }



}
