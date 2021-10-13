package com.goviesco.orders.enumeration;

public enum Status {

    PROCESSING("PROC"), COMPLETED("COMP"), CANCELED("CAN");

    private String dbData;

    private Status(String dbData) {
        this.dbData = dbData;
    }

    public String getDbColumn() {
        return this.dbData;
    }
}
