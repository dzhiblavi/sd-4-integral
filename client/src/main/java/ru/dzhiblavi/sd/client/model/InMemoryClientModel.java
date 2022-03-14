package ru.dzhiblavi.sd.client.model;

import ru.dzhiblavi.sd.client.entity.Client;
import ru.dzhiblavi.sd.client.entity.ClientStock;
import ru.dzhiblavi.sd.client.stock.StockClient;

import java.util.HashMap;
import java.util.Map;

public class InMemoryClientModel implements ClientModel {
    private final Map<String, Client> clientByName = new HashMap<>();
    private final StockClient stockClient;

    public InMemoryClientModel(final StockClient stockClient) {
        this.stockClient = stockClient;
    }

    private void checkContains(final String name) {
        if (!this.clientByName.containsKey(name)) {
            throw new IllegalArgumentException("Client '" + name + "' does not exist.");
        }
    }

    @Override
    synchronized public void addClient(final Client client) {
        if (this.clientByName.containsKey(client.getName())) {
            throw new IllegalArgumentException("Client '" + client.getName() + "' already exists.");
        }
        this.clientByName.put(client.getName(), client);
    }

    @Override
    synchronized public Client getClient(final String name) {
        checkContains(name);
        return this.clientByName.get(name);
    }

    @Override
    synchronized public void addFunds(final String name, final double delta) {
        checkContains(name);
        this.clientByName.get(name).changeFunds(delta);
    }

    @Override
    synchronized public boolean hasStock(final String name, final String stockName, final String companyName, final long quantity) {
        return getClient(name).getClientStocks().stream()
                .filter(
                        stock -> stock.getCompanyName().equals(companyName) && stock.getName().equals(stockName)
                )
                .mapToLong(ClientStock::getQuantity).sum()
                >= quantity;
    }

    @Override
    synchronized public void buyOrSell(final String name, final String stockName, final String companyName, final long quantityDelta) {
        if (quantityDelta < 0 && !this.hasStock(name, stockName, companyName, -quantityDelta)) {
            throw new IllegalArgumentException("Insufficient stocks for selling.");
        }
        final double cost = stockClient.modifyStock(stockName, companyName, -quantityDelta, 0.0);
        final Client client = this.getClient(name);
        if (client.getFunds() < cost * quantityDelta) {
            stockClient.modifyStock(stockName, companyName, quantityDelta, 0.0);
            throw new IllegalArgumentException("Insufficient funds: " + client.getFunds() + " < " + cost * quantityDelta);
        }
        client.changeFunds(-cost * quantityDelta);
        client.changeStockCount(stockName, companyName, quantityDelta);
    }

    @Override
    public double totalValue(final String name) {
        final Client client = this.getClient(name);
        return client.getFunds() + client.getClientStocks().stream()
                .mapToDouble(stock -> stock.getQuantity() * this.stockClient.queryPrice(stock.getQualifiedName()))
                .sum();
    }

    @Override
    public double queryPrice(final String stockName) {
        return this.stockClient.queryPrice(stockName);
    }
}
