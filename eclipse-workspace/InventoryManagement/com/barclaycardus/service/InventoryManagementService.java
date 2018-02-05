package com.barclaycardus.service;

import com.barclaycardus.model.BuySell;
import com.barclaycardus.model.Product;
import com.barclaycardus.util.OperationsEnum;

/**
 * Created by Rohita on 2/4/2018.
 */
public class InventoryManagementService {
    public static InventoryManagementService inventoryManagementService;
    private InventoryCache inventoryCache = InventoryCache.getInstance();

    public static InventoryManagementService getInstance() {
        if (inventoryManagementService == null) {
            inventoryManagementService = new InventoryManagementService();
        }
        return inventoryManagementService;
    }

    //Restrict from creating multiple objects
    private InventoryManagementService() {
    }

    public Boolean issueCommand(String command) {
        String[] parsedLineItemArray = command.split(" ");
        if (parsedLineItemArray != null && parsedLineItemArray.length >= 1) {
            OperationsEnum operationsEnum = OperationsEnum.fromValue(parsedLineItemArray[0]);
            if (operationsEnum == null) {
                System.out.println("Invalid command issued");
                return null;
            }
            String prodName = null;
            if (operationsEnum != OperationsEnum.REPORT) {
                prodName = parsedLineItemArray[1];
            }
            switch (operationsEnum) {
                case CREATE:
                    if (parsedLineItemArray.length == 4) {
                        if (inventoryCache.getProducts().containsKey(prodName)) {
                            if (inventoryCache.getProducts().get(prodName).getStatus()) {
                                //If product is already removed and if you are trying to recreate with same name, just replace
                                System.out.println("Invalid Create command - product already exists. Use Update command");
                            } else {
                                createProductFromInput(parsedLineItemArray);
                            }
                        } else {
                            createProductFromInput(parsedLineItemArray);
                        }
                    } else {
                        System.out.println("Invalid Create command - please check and try again");
                    }
                    break;
                case UPDATE_BUY:
                    updateProduct(parsedLineItemArray, prodName, true);
                    break;
                case UPDATE_SELL:
                    updateProduct(parsedLineItemArray, prodName, false);
                    break;
                case DELETE:
                    if (validateProduct(prodName)) {
                        inventoryCache.getReportHolder().getAvailableProducts().remove(prodName);
                        Product productTobeDeleted = inventoryCache.getProducts().get(prodName);
                        inventoryCache.getReportHolder().setDeletedPrice(inventoryCache.getReportHolder().getDeletedPrice() + productTobeDeleted.getQuantity() * productTobeDeleted.getBuySell().getBuyAt());
                        inventoryCache.getProducts().get(prodName).setStatus(false);
                    } else {
                        System.out.println("Invalid input - Product [" + prodName + " doesn't exist");
                    }
                    break;
                case REPORT:
                    System.out.println("\tInventory Report");
                    System.out.println("Item Name\tBought At\tSold At\tAvailable Qty\tValue");

                    Double total = 0d;
                    Double profit = 0d;
                    for(String productName: inventoryCache.getReportHolder().getAvailableProducts().keySet()) {
                        //Product reportProduct = inventoryCache.getReportHolder().getAvailableProducts().get(productName);
                        Product inventoryProduct = inventoryCache.getProducts().get(productName);
                        Integer initialQty = inventoryCache.getReportHolder().getAvailableProducts().get(productName);
                        Double currentValue = initialQty * inventoryProduct.getBuySell().getBuyAt() - inventoryProduct.getBuySell().getBuyAt() * (initialQty - inventoryProduct.getQuantity());
                        total += currentValue;
                        profit += (inventoryProduct.getBuySell().getSellAt() - inventoryProduct.getBuySell().getBuyAt())*(initialQty - inventoryProduct.getQuantity());
                        System.out.println(inventoryProduct.getProductName() + "\t" + inventoryProduct.getBuySell().getBuyAt() + "\t" + inventoryProduct.getBuySell().getSellAt() + "\t" + inventoryProduct.getQuantity() + "\t" + currentValue);
                    }
                    System.out.println("Total Value\t\t\t\t\t" + total);
                    System.out.println("Profit since previous report\t" + (profit  - inventoryCache.getReportHolder().getDeletedPrice()));
                    inventoryCache.getProducts().forEach((productName, i) -> {
                        if(inventoryCache.getProducts().get(productName).getStatus()) {
                            inventoryCache.getReportHolder().getAvailableProducts().put(productName, inventoryCache.getProducts().get(productName).getQuantity());
                        }
                    });
                    inventoryCache.getReportHolder().setDeletedPrice(0d);
                    break;
                default:
                    System.out.println("Default...");
            }
        }
        return null;
    }

    private void updateProduct(String[] parsedLineItemArray, String prodName, Boolean trueIfBuy) {
        if (parsedLineItemArray.length == 3) {
            if (validateProduct(prodName)) {
                Integer qty = validateQty(parsedLineItemArray[2]);
                if (qty != null) {
                    if (!trueIfBuy && inventoryCache.getProducts().get(prodName).getQuantity() < qty) {
                        System.out.println("Invalid qty - exceeds the available qty");
                    } else {
                        Integer updateQty = trueIfBuy ? (Math.addExact(inventoryCache.getProducts().get(prodName).getQuantity(), qty)) : (Math.subtractExact(inventoryCache.getProducts().get(prodName).getQuantity(), qty));
                        inventoryCache.getProducts().get(prodName).setQuantity(updateQty);
                        if (trueIfBuy) {
                            inventoryCache.getReportHolder().getAvailableProducts().put(prodName, inventoryCache.getReportHolder().getAvailableProducts().get(prodName) + updateQty);
                        }
                    }
                } else {
                    System.out.println("Invalid input - please check quantity");
                }
            } else {
                System.out.println("Invalid input - Product [" + prodName + " doesn't exist");
            }
        } else {
            System.out.println("Invalid Update command - please check and try again");
        }
    }

    private void createProductFromInput(String[] parsedLineItemArray) {
        try {
            Product product = new Product();
            String productName = parsedLineItemArray[1];
            product.setProductName(productName);
            BuySell buySell = new BuySell();
            buySell.setBuyAt(Double.parseDouble(parsedLineItemArray[2]));
            buySell.setSellAt(Double.parseDouble(parsedLineItemArray[3]));
            product.setBuySell(buySell);
            product.setQuantity(0);
            product.setStatus(true);
            inventoryCache.getProducts().put(productName, product);
            inventoryCache.getReportHolder().getAvailableProducts().put(productName, 0);
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid Price value");
        }
    }

    private Boolean validateProduct(String productName) {
        if (inventoryCache.getProducts().containsKey(productName)) {
            return true;
        }
        return false;
    }

    private Integer validateQty(String rawQty) {
        try {
            return Integer.parseInt(rawQty);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double validatePrice(String rawPrice) {
        try {
            return Double.parseDouble(rawPrice);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
