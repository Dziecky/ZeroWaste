package projekt.zespolowy.zero_waste.dto;

import projekt.zespolowy.zero_waste.entity.ProductCategory;

public class OrderStatsDTO {
    private ProductCategory category;
    private long orderCount;
    private double totalQuantity;
    private String unitOfMeasure;
    private double totalAmount;

    public OrderStatsDTO(ProductCategory category, long orderCount,
                         double totalQuantity, String unitOfMeasure,
                         double totalAmount) {
        this.category = category;
        this.orderCount = orderCount;
        this.totalQuantity = totalQuantity;
        this.unitOfMeasure = unitOfMeasure;
        this.totalAmount = totalAmount;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(long orderCount) {
        this.orderCount = orderCount;
    }

    public double getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}