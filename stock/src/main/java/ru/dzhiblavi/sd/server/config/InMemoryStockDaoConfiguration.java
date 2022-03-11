package ru.dzhiblavi.sd.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.dzhiblavi.sd.server.model.InMemoryStockModel;
import ru.dzhiblavi.sd.server.model.StockModel;

@Configuration
public class InMemoryStockDaoConfiguration {
    @Bean
    public StockModel stockDao() {
        return new InMemoryStockModel();
    }
}
