package projekt.zespolowy.zero_waste.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import projekt.zespolowy.zero_waste.entity.PaymentRequest;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.services.OrderService;
import projekt.zespolowy.zero_waste.services.ProductServiceImpl;
import projekt.zespolowy.zero_waste.services.UserService;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @PostMapping("/create-session")
    public Map<String, String> createCheckoutSession(
            @RequestBody PaymentRequest paymentRequest,
            Principal principal
    ) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/orders")
                .setCancelUrl("http://localhost:8080/orders")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("pln")
                                                .setUnitAmount((long) (paymentRequest.getAmount() * 100))
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(paymentRequest.getProductName())
                                                                .build()
                                                )
                                                .build()
                                )
                                .setQuantity(1L)
                                .build()
                )
                .build();

        Session session = Session.create(params);

        Product product = productService.handleProductAfterPurchase(paymentRequest.getProductName());
        User user = UserService.findByUsername(principal.getName());
        orderService.create(user, product);

        Map<String, String> response = new HashMap<>();
        response.put("id", session.getId());
        return response;
    }
}