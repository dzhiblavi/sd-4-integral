package ru.dzhiblavi.sd.client;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import ru.dzhiblavi.sd.client.config.LocalStockClient;
import ru.dzhiblavi.sd.client.stock.StockClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseTest {
    protected static class Stock {
        final String name;
        final String companyName;
        final double price;

        private Stock(String name, String companyName, double price) {
            this.name = name;
            this.companyName = companyName;
            this.price = price;
        }
    }

    @ClassRule
    public static GenericContainer stockWebServer
            = new FixedHostPortGenericContainer("stock:1.0-SNAPSHOT")
            .withFixedExposedPort(8080, 8080)
            .withExposedPorts(8080);

    protected static final StockClient stockClient = new LocalStockClient().stockClient();

    protected static final List<String> companyNames = List.of("google", "yandex", "intel");

    protected static final Map<String, List<Stock>> stocks = new HashMap<>();

    static {
        stocks.put("google", List.of(
                new Stock("s1", "google", 200.0),
                new Stock("s2", "google", 1000.0)
        ));
        stocks.put("yandex", List.of(
                new Stock("s2", "yandex", 20.0),
                new Stock("s3", "yandex", 100.0)
        ));
        stocks.put("intel", List.of(
                new Stock("s3", "intel", 1500.0),
                new Stock("s4", "intel", 50.0)
        ));
    }

    @BeforeClass
    public static void fillMarket() {
        for (final String companyName : companyNames) {
            stockClient.doPostRequest("new-company", Map.of("name", companyName));
        }
        stocks.forEach((companyName, stocksList) -> {
            stocksList.forEach(stock ->
                    stockClient.doPostRequest("new-stock", Map.of(
                            "name", stock.name,
                            "company", stock.companyName,
                            "quantity", String.valueOf(10),
                            "price", String.valueOf(stock.price)
                    ))
            );
        });
    }
}
