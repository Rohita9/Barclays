package com.barclaycardus.model;

/**
 * Created by Rohita on 2/4/2018.
 */
public class Product {
    private String productName;
    private BuySell buySell;
    private Integer quantity;
    private Boolean status;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BuySell getBuySell() {
        return buySell;
    }

    public void setBuySell(BuySell buySell) {
        this.buySell = buySell;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
