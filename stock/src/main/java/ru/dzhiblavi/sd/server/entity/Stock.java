package ru.dzhiblavi.sd.server.entity;

public class Stock {
    private final String name;
    private final String companyName;
    private long quantity;
    private double price;

    public Stock(final String name, final String companyName, final long quantity, final double price) {
        this.name = name;
        this.companyName = companyName;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(final long quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(final double price) {
        this.price = price;
    }
}
