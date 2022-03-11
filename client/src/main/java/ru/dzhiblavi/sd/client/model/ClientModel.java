package ru.dzhiblavi.sd.client.model;

import ru.dzhiblavi.sd.client.entity.Client;

public interface ClientModel {
    void addClient(final Client client);

    Client getClient(final String name);

    void addFunds(final String name, final double delta);

    boolean hasStock(final String name, final String stockName, final String companyName, final long quantity);

    void buyOrSell(final String name, final String stockName, final String companyName, final long quantity);

    double totalValue(final String name);

    double queryPrice(final String stockName);
}
