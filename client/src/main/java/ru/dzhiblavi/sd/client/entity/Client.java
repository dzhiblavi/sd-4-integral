package ru.dzhiblavi.sd.client.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
    private final String name;
    private double funds;
    private final Map<String, ClientStock> stockByQualifiedName = new HashMap<>();

    public Client(final String name, final double funds) {
        this.name = name;
        this.funds = funds;
    }

    public String getName() {
        return name;
    }

    public double getFunds() {
        return funds;
    }

    public void changeFunds(final double delta) {
        this.funds += delta;
    }

    public List<ClientStock> getClientStocks() {
        return new ArrayList<>(stockByQualifiedName.values());
    }

    public void changeStockCount(final String stockName, final String companyName, final long quantityDelta) {
        if (quantityDelta > 0) {
            this.addClientStock(new ClientStock(stockName, companyName, quantityDelta));
        } else if (quantityDelta < 0) {
            this.removeClientStocks(stockName, companyName, -quantityDelta);
        }
    }

    public void addClientStock(final ClientStock stock) {
        if (this.stockByQualifiedName.containsKey(stock.getQualifiedName())) {
            final ClientStock clientStock = this.stockByQualifiedName.get(stock.getQualifiedName());
            clientStock.changeQuantity(stock.getQuantity());
        } else {
            this.stockByQualifiedName.put(stock.getQualifiedName(), stock);
        }
    }

    public void removeClientStocks(final String stockName, final String companyName, final long quantity) {
        final ClientStock stock = this.stockByQualifiedName.get(ClientStock.getQualifiedName(stockName, companyName));
        stock.changeQuantity(-quantity);
        if (stock.getQuantity() == 0) {
            this.stockByQualifiedName.remove(ClientStock.getQualifiedName(stockName, companyName));
        }
    }
}
