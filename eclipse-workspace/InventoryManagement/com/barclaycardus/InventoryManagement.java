package com.barclaycardus;

import com.barclaycardus.service.InventoryManagementService;

import java.io.File;
import java.util.Scanner;

/**
 * Created by Rohita on 2/4/2018.
 */
public class InventoryManagement {
    private static InventoryManagementService inventoryManagementService = InventoryManagementService.getInstance();
    public static void main(String[] args) {
        try {
                Scanner inputStream = new Scanner(new File("barclayCardUs.txt"));
                while (inputStream.hasNext()) {
                    inventoryManagementService.issueCommand(inputStream.nextLine());
                }
                inputStream.close();
        } catch (Exception e) {
            System.err.println("Invalid file or file cant be read");
        }
    }
}
