package ru.dzhiblavi.sd.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.dzhiblavi.sd.server.model.CompanyModel;
import ru.dzhiblavi.sd.server.model.StockModel;
import ru.dzhiblavi.sd.server.entity.Company;
import ru.dzhiblavi.sd.server.entity.Stock;
import ru.dzhiblavi.sd.server.market.MarketState;
import ru.dzhiblavi.sd.server.market.MarketWatcher;

import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@RestController
public class StockController {
    private final MarketWatcher marketWatcher;
    private final StockModel stockDao;
    private final CompanyModel companyDao;

    private String execute(final Runnable runnable, final String onSuccess) {
        try {
            runnable.run();
            return onSuccess + System.lineSeparator();
        } catch (final Throwable t) {
            return "An error occurred: " + t.getMessage() + System.lineSeparator();
        }
    }

    private String execute(final Callable<String> callable) {
        try {
            return callable.call() + System.lineSeparator();
        } catch (final Throwable t) {
            return "An error occurred: " + t.getMessage() + System.lineSeparator();
        }
    }

    public StockController(final StockModel stockDao, final CompanyModel companyDao, final MarketState marketState) {
        this.stockDao = stockDao;
        this.companyDao = companyDao;
        this.marketWatcher = new MarketWatcher(marketState);
        this.marketWatcher.start();
    }

    @RequestMapping("/new-company")
    public String newCompany(@RequestParam("name") final String name) {
        return execute(
                () -> companyDao.addCompany(new Company(name)),
                "Company '" + name + "' has been successfully added."
        );
    }

    @RequestMapping("/new-stock")
    public String newStock(@RequestParam("name") final String name,
                           @RequestParam("company") final String companyName,
                           @RequestParam("price") final double price,
                           @RequestParam("quantity") final long quantity) {
        return execute(
                () -> this.stockDao.addStock(new Stock(name, companyName, quantity, price)),
                "New stock '" + name + "' by '" + companyName + "' has been successfully added."
        );
    }

    @RequestMapping("/stock-info")
    public String stockInfo() {
        return execute(() ->
                this.stockDao.getAllStocks().stream()
                        .map(stock -> stock.getCompanyName() + ": '" + stock.getName()
                                + "', quantity: " + stock.getQuantity() + ", price: " + stock.getPrice())
                        .collect(Collectors.joining(System.lineSeparator()))
        );
    }

    @RequestMapping("/modify-stock")
    public String modifyStock(@RequestParam("name") final String name,
                              @RequestParam("company") final String companyName,
                              @RequestParam(name = "qdelta", required = false, defaultValue = "0") final long quantityDelta,
                              @RequestParam(name = "pdelta", required = false, defaultValue = "0") final double priceDelta) {
        return execute(
                () -> {
                    final double price = this.stockDao.modifyStock(name, companyName, quantityDelta, priceDelta);
                    return "Successfully modified stock '" + name + "' by '" + companyName + "', price: " + price;
                }
        );
    }
}
