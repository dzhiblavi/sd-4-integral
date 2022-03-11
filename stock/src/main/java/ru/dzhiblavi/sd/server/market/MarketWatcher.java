package ru.dzhiblavi.sd.server.market;

public class MarketWatcher {
    private final static long UPDATE_INTERVAL = 1000;
    private Thread watcherThread;
    private final MarketState marketState;

    public MarketWatcher(final MarketState marketState) {
        this.marketState = marketState;
    }

    public void start() {
        watcherThread = new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    Thread.sleep(UPDATE_INTERVAL);
                    marketState.updateState();
                }
            } catch (final InterruptedException e) {
                // ignore
            }
        });
        watcherThread.start();
    }

    public void stop() {
        try {
            watcherThread.interrupt();
            watcherThread.join();
        } catch (final InterruptedException e) {
            // ignore
        }
    }
}
