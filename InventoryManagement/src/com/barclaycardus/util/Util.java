package com.barclaycardus.util;

/**
 * Created by Rohita on 2/5/2018.
 */
public class Util {
    public static Integer validateQty(String rawQty) {
        try {
            return Integer.parseInt(rawQty);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double validatePrice(String rawPrice) {
        try {
            return Double.parseDouble(rawPrice);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
