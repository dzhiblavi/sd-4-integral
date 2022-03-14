package ru.dzhiblavi.sd.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private ResponseEntity<?> execute(final Callable<String> callable) {
        try {
            return new ResponseEntity<>(callable.call() + System.lineSeparator(), HttpStatus.OK);
        } catch (final IllegalArgumentException e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage() + System.lineSeparator(), HttpStatus.BAD_REQUEST);
        } catch (final Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public StockController(final StockModel stockDao, final CompanyModel companyDao, final MarketState marketState) {
        this.stockDao = stockDao;
        this.companyDao = companyDao;
        this.marketWatcher = new MarketWatcher(marketState);
        this.marketWatcher.start();
    }

    @RequestMapping("/new-company")
    public ResponseEntity<?> newCompany(@RequestParam("name") final String name) {
        return execute(() -> {
            companyDao.addCompany(new Company(name));
            return "Company '" + name + "' has been successfully added.";
        });
    }

    @RequestMapping("/new-stock")
    public ResponseEntity<?> newStock(@RequestParam("name") final String name,
                                      @RequestParam("company") final String companyName,
                                      @RequestParam("price") final double price,
                                      @RequestParam("quantity") final long quantity) {
        return execute(() -> {
            this.stockDao.addStock(new Stock(name, companyName, quantity, price));
            return "New stock '" + name + "' by '" + companyName + "' has been successfully added.";
        });
    }

    @RequestMapping("/stock-info")
    public ResponseEntity<?> stockInfo() {
        return execute(() ->
                this.stockDao.getAllStocks().stream()
                        .map(stock -> "'" + stock.getName() + ":" + stock.getCompanyName()
                                + "', quantity: " + stock.getQuantity() + ", price: " + stock.getPrice())
                        .collect(Collectors.joining(System.lineSeparator()))
        );
    }

    @RequestMapping("/modify-stock")
    public ResponseEntity<?> modifyStock(@RequestParam("name") final String name,
                                         @RequestParam("company") final String companyName,
                                         @RequestParam(name = "qdelta", required = false, defaultValue = "0") final long quantityDelta,
                                         @RequestParam(name = "pdelta", required = false, defaultValue = "0") final double priceDelta) {
        return execute(() -> {
            final double price = this.stockDao.modifyStock(name, companyName, quantityDelta, priceDelta);
            return "Successfully modified stock '" + name + "' by '" + companyName + "', price: " + price;
        });
    }
}
