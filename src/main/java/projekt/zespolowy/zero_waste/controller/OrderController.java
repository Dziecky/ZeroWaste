package projekt.zespolowy.zero_waste.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import projekt.zespolowy.zero_waste.dto.OrderDTO;
import projekt.zespolowy.zero_waste.dto.user.OrderSummaryDTO;
import projekt.zespolowy.zero_waste.entity.Order;
import projekt.zespolowy.zero_waste.entity.Product;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.services.OrderService;
import projekt.zespolowy.zero_waste.services.ProductService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;

    @GetMapping("/orders")
    public String viewOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Principal principal,
            Model model
    ) {

        User user = userService.findByUsername(principal.getName());

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Order> ordersPage = orderService.getOrdersByUser(user, pageable);

        List<OrderSummaryDTO> orderDTOS = ordersPage.stream()
                .map(order -> productService.getProductById(order.getProduct().getId())
                        .map(product -> new OrderSummaryDTO(order.getId(), product.getPrice()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        model.addAttribute("orders", orderDTOS);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());

        return "orders/orders";
    }

    @GetMapping("/orders/{orderId}")
    public String getOrderDetails(@PathVariable("orderId") String orderId, Model model) {
        OrderDTO orderDTO = getOrderDTOByOrderId(orderId);
        model.addAttribute("order", orderDTO);
        return "orders/order-details";
    }

    @GetMapping("/orders/invoice/{orderId}")
    public void generateInvoice(@PathVariable String orderId, HttpServletResponse response, Principal principal) throws IOException, DocumentException {
        OrderDTO order = getOrderDTOByOrderId(orderId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=invoice_" + orderId + ".pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, BaseColor.GREEN);
        Paragraph title = new Paragraph("Zero Waste", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Font sloganFont = new Font(Font.FontFamily.HELVETICA, 14, Font.ITALIC, BaseColor.DARK_GRAY);
        Paragraph slogan = new Paragraph("Making the World Greener", sloganFont);
        slogan.setAlignment(Element.ALIGN_CENTER);
        document.add(slogan);
        document.add(new Paragraph("\n"));

        Font invoiceTitleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font invoiceSubitleFont = new Font(Font.FontFamily.HELVETICA, 14);
        Paragraph invoiceTitle = new Paragraph("Invoice #" + orderId, invoiceTitleFont);
        invoiceTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(invoiceTitle);
        Paragraph invoiceSubtitle = new Paragraph("Thanks for your purchase, %s!".formatted(principal.getName()), invoiceSubitleFont);
        invoiceSubtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(invoiceSubtitle);
        document.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        table.addCell(getHeaderCell("Order ID"));
        table.addCell(getCell(order.getOrderId().toString()));

        table.addCell(getHeaderCell("Product Name"));
        table.addCell(getCell(order.getProductName()));

        table.addCell(getHeaderCell("Quantity"));
        table.addCell(getCell(String.valueOf(order.getQuantity())));

        table.addCell(getHeaderCell("Total Price"));
        table.addCell(getCell(order.getPrice() + " PLN"));

        table.addCell(getHeaderCell("Bought From"));
        table.addCell(getCell(order.getOwnerName()));

        table.addCell(getHeaderCell("Bought At"));
        table.addCell(getCell(order.getCreatedAt().format(formatter)));

        document.add(table);

        document.add(new Paragraph("\n"));
        Font footerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, BaseColor.GRAY);
        Paragraph generatedAt = new Paragraph("Generated at %s.".formatted(LocalDateTime.now().format(formatter)), footerFont);
        Paragraph footer = new Paragraph("Thank you for supporting sustainability with Zero Waste!", footerFont);
        generatedAt.setAlignment(Element.ALIGN_CENTER);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(generatedAt);
        document.add(footer);

        document.close();
    }

    private PdfPCell getHeaderCell(String text) {
        Font font = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8);
        return cell;
    }

    private PdfPCell getCell(String text) {
        Font font = new Font(Font.FontFamily.HELVETICA, 12);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8);
        return cell;
    }

    private OrderDTO getOrderDTOByOrderId(String orderId) {
        Long id = Long.parseLong(orderId);
        Order order = orderService.getOrderById(id);
        Optional<Product> maybeProduct = productService.getProductById(order.getProduct().getId());
        OrderDTO orderDTO = null;
        if(maybeProduct.isPresent()) {
            Product product = maybeProduct.get();
            orderDTO = new OrderDTO(
                    order.getId(),
                    product.getName(),
                    product.getQuantity(),
                    product.getPrice(),
                    product.getImageUrl(),
                    product.getOwner().getUsername(),
                    order.getCreatedAt()
            );
        }
        return orderDTO;
    }
}
