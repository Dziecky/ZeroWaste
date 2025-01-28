package projekt.zespolowy.zero_waste.dto;

import projekt.zespolowy.zero_waste.entity.ProductCategory;
import projekt.zespolowy.zero_waste.entity.UnitOfMeasure;

import java.time.LocalDateTime;
import java.util.Set;

public class ProductDTO {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private boolean available;
    private double price;
    private LocalDateTime createdAt;
    private ProductCategory productCategory;
    private double quantity;
    private UnitOfMeasure unitOfMeasure;
    private boolean auction;
    private LocalDateTime endDate;
    private Set<String> tagNames;


    public ProductDTO(Long id, String name, String description, String imageUrl, boolean available, double price,
                      LocalDateTime createdAt, ProductCategory productCategory, double quantity, UnitOfMeasure unitOfMeasure,
                      boolean auction, LocalDateTime endDate, Set<String> tagNames) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.available = available;
        this.price = price;
        this.createdAt = createdAt;
        this.productCategory = productCategory;
        this.quantity = quantity;
        this.unitOfMeasure = unitOfMeasure;
        this.auction = auction;
        this.endDate = endDate;
        this.tagNames = tagNames;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public boolean isAuction() {
        return auction;
    }

    public void setAuction(boolean auction) {
        this.auction = auction;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Set<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(Set<String> tagNames) {
        this.tagNames = tagNames;
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", available=" + available +
                ", price=" + price +
                ", createdAt=" + createdAt +
                ", productCategory=" + productCategory +
                ", quantity=" + quantity +
                ", unitOfMeasure=" + unitOfMeasure +
                ", auction=" + auction +
                ", endDate=" + endDate +
                ", tagNames=" + tagNames +
                '}';
    }
}
