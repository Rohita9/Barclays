package com.barclaycardus.service;

import com.barclaycardus.model.BuySell;
import com.barclaycardus.model.Product;
import com.barclaycardus.model.ReportHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rohita on 2/4/2018.
 */
public class InventoryCache {
    private static InventoryCache inventoryCache = getInstance();
    private static Map<String, Product> products;

    private static ReportHolder reportHolder;

    static {
        //Don't expose setters for cached products/reportHolder - it should be one time within static block
        products = new HashMap<>();
        reportHolder = new ReportHolder();
        reportHolder.setAvailableProducts(new HashMap<>());
        reportHolder.setDeletedPrice(0d);
    }

    public static InventoryCache getInstance() {
        if (inventoryCache == null) {
            inventoryCache = new InventoryCache();
        }
        return inventoryCache;
    }

    //Restrict from creating multiple objects
    private InventoryCache() {
    }

    public Map<String, Product> getProducts() {
        return products;
    }

    public ReportHolder getReportHolder() {
        return reportHolder;
    }

}
