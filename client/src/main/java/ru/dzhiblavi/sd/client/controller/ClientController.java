package ru.dzhiblavi.sd.client.controller;

import org.springframework.web.bind.annotation.*;
import ru.dzhiblavi.sd.client.model.ClientModel;
import ru.dzhiblavi.sd.client.entity.Client;

import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@RestController
public class ClientController {
    private final ClientModel clientModel;

    private String execute(final Callable<String> callable) {
        try {
            return callable.call() + System.lineSeparator();
        } catch (final Throwable t) {
            return "An error occurred: " + t.getMessage() + System.lineSeparator();
        }
    }

    public ClientController(final ClientModel clientModel) {
        this.clientModel = clientModel;
    }

    @RequestMapping("/new-user")
    public String newUser(@RequestParam("name") final String name,
                          @RequestParam(name = "funds", required = false, defaultValue = "0") final double funds) {
        return execute(() -> {
            this.clientModel.addClient(new Client(name, funds));
            return "Client '" + name + "' has been successfully added.";
        });
    }

    @RequestMapping("/add-funds")
    public String addFunds(@RequestParam("name") final String name, @RequestParam("delta") final double delta) {
        return execute(() -> {
            this.clientModel.addFunds(name, delta);
            return "Funds have been successfully added to '" + name + "'";
        });
    }

    @RequestMapping("/get-stocks")
    public String getStocksList(@RequestParam("name") final String name) {
        return execute(() ->
                this.clientModel.getClient(name).getClientStocks().stream()
                        .map(stock -> stock.getName() + ": " + stock.getQuantity() + " x " + this.clientModel.queryPrice(stock.getQualifiedName()))
                        .collect(Collectors.joining(System.lineSeparator()))
        );
    }

    @RequestMapping("/get-total")
    public String getTotalValue(@RequestParam("name") final String name) {
        return execute(() -> name + "'s value is " + this.clientModel.totalValue(name));
    }

    @RequestMapping("/buy-sell")
    public String buyOrSell(@RequestParam("name") final String name,
                            @RequestParam("stock-name") final String stockName,
                            @RequestParam("company-name") final String companyName,
                            @RequestParam("delta") final long delta) {
        return execute(() -> {
            if (delta < 0 && !this.clientModel.hasStock(name, stockName, companyName, -delta)) {
                return "Client cannot sell this stock." + System.lineSeparator();
            }
            this.clientModel.buyOrSell(name, stockName, companyName, delta);
            return name + " successfully bought or sold " + delta + " units of '" + stockName + "'";
        });
    }
}
