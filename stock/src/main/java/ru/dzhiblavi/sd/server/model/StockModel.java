package ru.dzhiblavi.sd.server.model;

import ru.dzhiblavi.sd.server.entity.Stock;

import java.util.List;

public interface StockModel {
    void addStock(final Stock stock);

    Stock getStock(final String companyName, final String name);

    List<Stock> getAllStocks();

    double modifyStock(final String name, final String companyName, final long quantityDelta, final double priceDelta);
}
