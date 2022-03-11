package ru.dzhiblavi.sd.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.dzhiblavi.sd.client.model.ClientModel;
import ru.dzhiblavi.sd.client.model.InMemoryClientModel;
import ru.dzhiblavi.sd.client.stock.StockClient;

@Configuration
public class InMemoryClientDaoConfig {
    @Bean
    public ClientModel clientDao(final StockClient stockClient) {
        return new InMemoryClientModel(stockClient);
    }
}
