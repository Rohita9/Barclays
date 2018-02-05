package com.barclaycardus.util;

/**
 * Created by Rohita on 2/4/2018.
 */
public enum OperationsEnum {
    CREATE("create"), UPDATE_BUY("updateBuy"), UPDATE_SELL("updateSell"), DELETE("delete"), REPORT("report");

    private final String operation;

    OperationsEnum(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return this.operation;
    }

    public String toString() {
        return String.valueOf(operation);
    }

    public static OperationsEnum fromValue(String oper) {
        for (OperationsEnum operationsEnum : OperationsEnum.values()) {
            if (String.valueOf(operationsEnum.operation).equals(oper)) {
                return operationsEnum;
            }
        }
        return null;
    }
}
