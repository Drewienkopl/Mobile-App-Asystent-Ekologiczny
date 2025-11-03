package com.example.lab1.data;

public class Product {
    private long id;
    private String name;
    private double price;
    private String expiryDate; // format ISO: yyyy-MM-dd
    private String category;
    private String description;
    private String store;
    private String purchaseDate; // format ISO: yyyy-MM-dd

    public Product() {}

    public Product(long id, String name, double price, String expiryDate, String category,
                   String description, String store, String purchaseDate) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.expiryDate = expiryDate;
        this.category = category;
        this.description = description;
        this.store = store;
        this.purchaseDate = purchaseDate;
    }
    public long getId(){ return id;}
    public String getName(){ return name;}
    public double getPrice(){ return price;}
    public String getExpiryDate(){ return expiryDate;}
    public String getCategory(){ return category;}
    public String getDescription(){ return description;}
    public String getStore(){ return store;}
    public String getPurchaseDate(){ return purchaseDate;}

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

}
