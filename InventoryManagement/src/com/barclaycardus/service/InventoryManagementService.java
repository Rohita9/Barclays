package com.barclaycardus.service;

import com.barclaycardus.model.BuySell;
import com.barclaycardus.model.Product;
import com.barclaycardus.util.OperationsEnum;
import com.barclaycardus.util.Status;
import com.barclaycardus.util.Util;

import java.time.LocalDateTime;
import java.util.Date;

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
                System.err.println("Invalid command issued");
                return null;
            }
            String prodName = null;
            if ((operationsEnum != OperationsEnum.REPORT) && (operationsEnum != OperationsEnum.EOI)) {
                prodName = parsedLineItemArray[1];
            }
            switch (operationsEnum) {
                case CREATE:
                    if (parsedLineItemArray.length == 4) {
                        if (validateProduct(prodName)) {
                            if (inventoryCache.getProducts().get(prodName).getStatus() == Status.ACTIVE) {
                                System.err.println("Invalid Create command - product already exists. Use Update command");
                            } else {
                                //If product is already removed and if you are trying to recreate with same name, just replace
                                createProductFromInput(parsedLineItemArray);
                            }
                        } else {
                            createProductFromInput(parsedLineItemArray);
                        }
                    } else {
                        System.err.println("Invalid Create command - please check and try again");
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
                        inventoryCache.getProducts().get(prodName).setStatus(Status.INACTIVE);
                    } else {
                        System.err.println("Invalid input - Product [" + prodName + " doesn't exist/inactive");
                    }
                    break;
                case REPORT:
                    System.out.println("\tInventory Report");
                    System.out.println("Item Name\tBought At\tSold At\tAvailable Qty\tValue");

                    Double total = 0d;
                    Double profit = 0d;
                    for (String productName : inventoryCache.getReportHolder().getAvailableProducts().keySet()) {
                        Product inventoryProduct = inventoryCache.getProducts().get(productName);
                        if (inventoryProduct.getStatus() != Status.INACTIVE) {
                            Integer initialQty = inventoryCache.getReportHolder().getAvailableProducts().get(productName);
                            Double currentValue = initialQty * inventoryProduct.getBuySell().getBuyAt() - inventoryProduct.getBuySell().getBuyAt() * (initialQty - inventoryProduct.getQuantity());
                            if (inventoryProduct.getStatus() == Status.ACTIVE) {
                                //Consider only ACTIVE status product in the totals
                                total += currentValue;
                            }
                            profit += (inventoryProduct.getBuySell().getSellAt() - inventoryProduct.getBuySell().getBuyAt()) * (initialQty - inventoryProduct.getQuantity());
                            System.out.println(inventoryProduct.getProductName() + "\t" + inventoryProduct.getBuySell().getBuyAt() + "\t" + inventoryProduct.getBuySell().getSellAt() + "\t" + inventoryProduct.getQuantity() + "\t" + currentValue);
                        }
                    }
                    System.out.println("Total Value\t\t\t\t\t" + total);
                    System.out.println("Profit since previous report\t" + (profit - inventoryCache.getReportHolder().getDeletedPrice()));
                    inventoryCache.getProducts().forEach((productName, i) -> {
                        if (inventoryCache.getProducts().get(productName).getStatus() == Status.ACTIVE) {
                            inventoryCache.getReportHolder().getAvailableProducts().put(productName, inventoryCache.getProducts().get(productName).getQuantity());
                        }
                    });
                    inventoryCache.getReportHolder().setDeletedPrice(0d);
                    break;
                case UPDATE_SELL_PRICE:
                    if (parsedLineItemArray.length == 3) {
                        if (inventoryCache.getProducts().containsKey(prodName)) {
                            updateSalePrice(parsedLineItemArray);
                        }
                    } else {
                        System.err.println("Invalid Update Sale Price input - Use for sample 'updateSalePrice Book01 3.98");
                    }
                    break;
                case EOI:
                    System.out.println("End of input");
                    break;
                default:
                    System.err.println("Default...");
            }
        }
        return null;
    }

    private Boolean updateSalePrice(String[] parsedLineItemArray) {
        String productName = parsedLineItemArray[1];
        Double newPrice = Util.validatePrice(parsedLineItemArray[2]);
        if (newPrice != null) {
            Product productToUpdate = inventoryCache.getProducts().get(productName);
            productToUpdate.setStatus(Status.OLD);
            productToUpdate.setUpdatedAt(LocalDateTime.now());
            String productNameWithoutVersion = productName.replaceAll("[^a-zA-Z]", "");
            String currentVersion = productName.replaceAll("[^0-9]", "");
            if (currentVersion == null || currentVersion.isEmpty()) {
                System.err.println("Product name shud have version included");
                return false;
            }
            String newProductName = productNameWithoutVersion.concat("0") + (Integer.parseInt(currentVersion) + 1);

            Product versionedProduct = new Product();
            versionedProduct.setProductName(newProductName);
            versionedProduct.setBuySell(new BuySell(productToUpdate.getBuySell().getBuyAt(), newPrice));
            versionedProduct.setQuantity(productToUpdate.getQuantity());
            versionedProduct.setStatus(Status.ACTIVE);
            versionedProduct.setCreatedAt(LocalDateTime.now());
            versionedProduct.setUpdatedAt(LocalDateTime.now());
            inventoryCache.getProducts().put(newProductName, versionedProduct);
            inventoryCache.getReportHolder().getAvailableProducts().put(newProductName, productToUpdate.getQuantity());
        }
        return false;
    }

    private Boolean updateProduct(String[] parsedLineItemArray, String prodName, Boolean trueIfBuy) {
        if (parsedLineItemArray.length == 3) {
            if (validateProduct(prodName)) {
                Integer qty = Util.validateQty(parsedLineItemArray[2]);
                if (qty != null) {
                    if (!trueIfBuy && inventoryCache.getProducts().get(prodName).getQuantity() < qty) {
                        System.err.println("Invalid qty - exceeds the available qty");
                    } else {
                        Integer updateQty = trueIfBuy ? (Math.addExact(inventoryCache.getProducts().get(prodName).getQuantity(), qty)) : (Math.subtractExact(inventoryCache.getProducts().get(prodName).getQuantity(), qty));
                        inventoryCache.getProducts().get(prodName).setQuantity(updateQty);
                        inventoryCache.getProducts().get(prodName).setUpdatedAt(LocalDateTime.now());
                        if (trueIfBuy) {
                            inventoryCache.getReportHolder().getAvailableProducts().put(prodName, inventoryCache.getReportHolder().getAvailableProducts().get(prodName) + updateQty);
                        }
                        return true;
                    }
                } else {
                    System.err.println("Invalid input - please check quantity");
                }
            } else {
                System.err.println("Invalid input - Product [" + prodName + " doesn't exist");
            }
        } else {
            System.err.println("Invalid Update command - please check and try again");
        }
        return false;
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
            product.setStatus(Status.ACTIVE);
            product.setCreatedAt(LocalDateTime.now());
            product.setUpdatedAt(LocalDateTime.now());
            inventoryCache.getProducts().put(productName, product);
            inventoryCache.getReportHolder().getAvailableProducts().put(productName, 0);
        } catch (NumberFormatException nfe) {
            System.err.println("Invalid Price value");
        }
    }

    private Boolean validateProduct(String productName) {
        if (inventoryCache.getProducts().containsKey(productName) && inventoryCache.getProducts().get(productName).getStatus() == Status.ACTIVE) {
            return true;
        }
        return false;
    }

}
