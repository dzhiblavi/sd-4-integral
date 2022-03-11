package ru.dzhiblavi.sd.client.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
    private final String name;
    private double funds;
    private final Map<String, ClientStock> stockByName = new HashMap<>();

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
        return new ArrayList<>(stockByName.values());
    }

    public void changeStockCount(final String stockName, final String companyName, final long quantityDelta) {
        if (quantityDelta > 0) {
            this.addClientStock(new ClientStock(stockName, companyName, quantityDelta));
        } else if (quantityDelta < 0) {
            this.removeClientStocks(stockName, -quantityDelta);
        }
    }

    public void addClientStock(final ClientStock stock) {
        if (this.stockByName.containsKey(stock.getName())) {
            final ClientStock clientStock = this.stockByName.get(stock.getName());
            clientStock.changeQuantity(stock.getQuantity());
        } else {
            this.stockByName.put(stock.getName(), stock);
        }
    }

    public void removeClientStocks(final String stockName, final long quantity) {
        this.stockByName.get(stockName).changeQuantity(-quantity);
    }
}
