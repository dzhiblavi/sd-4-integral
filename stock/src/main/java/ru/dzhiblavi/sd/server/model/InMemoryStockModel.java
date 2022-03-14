package ru.dzhiblavi.sd.server.model;

import ru.dzhiblavi.sd.server.entity.Stock;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryStockModel implements StockModel {
    private final Map<String, List<Stock>> stocksByCompanyName = new HashMap<>();

    private Optional<Stock> getStockOptional(final String companyName, final String stockName) {
        if (!stocksByCompanyName.containsKey(companyName)) {
            return Optional.empty();
        }
        return stocksByCompanyName.get(companyName).stream()
                .filter(stock -> stock.getName().equals(stockName))
                .findFirst();
    }

    @Override
    public void addStock(final Stock stock) {
        final String companyName = stock.getCompanyName();
        if (getStockOptional(companyName, stock.getName()).isPresent()) {
            throw new IllegalArgumentException(
                    "Stock '" + stock.getName() + "' is already present. Consider modifying it."
            );
        }
        stocksByCompanyName.putIfAbsent(companyName, new ArrayList<>());
        stocksByCompanyName.get(companyName).add(stock);
    }

    @Override
    public Stock getStock(final String companyName, final String stockName) {
        final Optional<Stock> stockOptional = getStockOptional(companyName, stockName);
        if (stockOptional.isEmpty()) {
            throw new IllegalArgumentException("Stock '" + stockName + "' by '" + companyName + "' is not found.");
        }
        return stockOptional.get();
    }

    @Override
    public List<Stock> getAllStocks() {
        return stocksByCompanyName.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public double modifyStock(final String stockName, final String companyName,
                              final long quantityDelta, final double priceDelta) {
        final Optional<Stock> stockOptional = getStockOptional(companyName, stockName);
        if (stockOptional.isEmpty()) {
            throw new IllegalArgumentException(
                    "Stock '" + stockName + "' by '" + companyName + "' is not present, so cannot be modified."
            );
        }
        final Stock stock = stockOptional.get();
        final long newQuantity = stock.getQuantity() + quantityDelta;
        final double newPrice = stock.getPrice() + priceDelta;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("New stock's quantity will be " + newQuantity + ", impossible.");
        }
        if (newPrice <= 0.0) {
            throw new IllegalArgumentException("New stock's price will be " + newPrice + ", impossible");
        }
        stockOptional.get().setQuantity(newQuantity);
        stockOptional.get().setPrice(newPrice);
        return newPrice;
    }
}
