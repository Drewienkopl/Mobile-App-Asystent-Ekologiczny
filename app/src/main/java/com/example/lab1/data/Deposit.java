package com.example.lab1.data;

public class Deposit {
    private long id;
    private String type;
    private double value;
    private String barcode;
    private boolean returned;

    public Deposit() {}

    public Deposit(long id, String type, double value, String barcode, boolean returned) {
        this.id = id;
        this.type = type;
        this.value = value;
        this.barcode = barcode;
        this.returned = returned;
    }

    public long getId() {return id; }
    public String getType() {return type; }
    public double getValue() {return value; }
    public String getBarcode() {return barcode; }
    public boolean isReturned() {return returned; }

    public void setId(long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}
