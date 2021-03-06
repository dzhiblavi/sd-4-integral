package ru.dzhiblavi.sd.client.entity;

public class ClientStock {
    private final String name;
    private final String companyName;
    private long quantity;

    public ClientStock(final String name, final String companyName, final long quantity) {
        this.name = name;
        this.companyName = companyName;
        this.quantity = quantity;
    }

    public String getQualifiedName() {
        return getQualifiedName(this.name, this.companyName);
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

    public void changeQuantity(final long quantityDelta) {
        this.quantity += quantityDelta;
    }

    public static String getQualifiedName(final String stockName, final String companyName) {
        return stockName + ":" + companyName;
    }
}
