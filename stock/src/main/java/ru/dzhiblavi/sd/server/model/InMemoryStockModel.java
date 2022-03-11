package ru.dzhiblavi.sd.server.model;

import ru.dzhiblavi.sd.server.entity.Stock;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryStockModel implements StockModel {
    private final Map<String, List<Stock>> stocksByCompanyName = new HashMap<>();

    synchronized private Optional<Stock> getStockOptional(final String companyName, final String name) {
        if (!stocksByCompanyName.containsKey(companyName)) {
            return Optional.empty();
        }
        final List<Stock> stocks = stocksByCompanyName.get(companyName);
        return stocks.stream()
                .filter(stock -> stock.getName().equals(name))
                .findFirst();
    }

    @Override
    synchronized public void addStock(final Stock stock) {
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
    synchronized public Stock getStock(final String companyName, final String name) {
        final Optional<Stock> stockOptional = getStockOptional(companyName, name);
        if (stockOptional.isEmpty()) {
            throw new IllegalArgumentException("Stock '" + name + "' by '" + companyName + "' is not found.");
        }
        return stockOptional.get();
    }

    @Override
    synchronized public List<Stock> getAllStocks() {
        return stocksByCompanyName.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    synchronized public double modifyStock(final String name, final String companyName,
                                           final long quantityDelta, final double priceDelta) {
        final Optional<Stock> stockOptional = getStockOptional(companyName, name);
        if (stockOptional.isEmpty()) {
            throw new IllegalArgumentException(
                    "Stock '" + name + "' by '" + companyName + "' is not present, so cannot be modified."
            );
        }
        final Stock stock = stockOptional.get();
        final long newQuantity = stock.getQuantity() + quantityDelta;
        final double newPrice = stock.getPrice() + priceDelta;
        if (newQuantity < 0 || newPrice <= 0.0) {
            throw new IllegalArgumentException("Failed to modify '" + name + "' by '" + companyName);
        }
        stockOptional.get().setQuantity(newQuantity);
        stockOptional.get().setPrice(newPrice);
        return newPrice;
    }
}
