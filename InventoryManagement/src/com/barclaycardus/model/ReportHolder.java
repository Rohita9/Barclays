package com.barclaycardus.model;

import java.util.List;
import java.util.Map;

/**
 * Created by Rohita on 2/4/2018.
 */
public class ReportHolder {
    //ProductName, InitialQty
    private Map<String, Integer> availableProducts;
    private Double deletedPrice;

    public Map<String, Integer> getAvailableProducts() {
        return availableProducts;
    }

    public void setAvailableProducts(Map<String, Integer> availableProducts) {
        this.availableProducts = availableProducts;
    }

    public Double getDeletedPrice() {
        return deletedPrice;
    }

    public void setDeletedPrice(Double deletedPrice) {
        this.deletedPrice = deletedPrice;
    }
}
