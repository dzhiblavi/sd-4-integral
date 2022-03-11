package ru.dzhiblavi.sd.server.market;

import ru.dzhiblavi.sd.server.model.StockModel;

import java.util.Random;

public class RandomMarketState implements MarketState {
    private final static double MIN_PRICE = 0.1;
    private final static double CHANGE_WINDOW_WIDTH = 1.0;
    private final StockModel stockDao;
    private final Random random = new Random(239);

    public RandomMarketState(final StockModel stockDao) {
        this.stockDao = stockDao;
    }

    @Override
    public void updateState() {
        this.stockDao.getAllStocks().forEach(stock -> this.stockDao.modifyStock(
                stock.getName(),
                stock.getCompanyName(),
                0,
                Math.max(-stock.getPrice() + MIN_PRICE, -CHANGE_WINDOW_WIDTH / 2.0 + CHANGE_WINDOW_WIDTH * random.nextDouble())
        ));
    }
}
