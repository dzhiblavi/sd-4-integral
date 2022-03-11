package ru.dzhiblavi.sd.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.dzhiblavi.sd.server.model.StockModel;
import ru.dzhiblavi.sd.server.market.MarketState;
import ru.dzhiblavi.sd.server.market.RandomMarketState;

@Configuration
public class RandomMarketStateConfig {
    @Bean
    public MarketState marketState(final StockModel stockDao) {
        return new RandomMarketState(stockDao);
    }
}
