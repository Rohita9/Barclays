package com.barclaycardus;

import com.barclaycardus.service.InventoryManagementService;
import com.barclaycardus.util.OperationsEnum;

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
                String input = inputStream.nextLine();
                inventoryManagementService.issueCommand(input);
            }
            inputStream.close();
        } catch (Exception e) {
            System.err.println("Invalid file or file cant be read");
        }
    }
}
