package ru.dzhiblavi.sd.client;

import org.junit.Assert;
import org.junit.Test;

public class StockClientTest extends BaseTest {
    @Test
    public void testQueryPriceNoSuchStock() {
        Assert.assertThrows(IllegalArgumentException.class, () -> stockClient.queryPrice("no-such-stock"));
    }

    @Test
    public void testModifyStockNoSuchStock() {
        Assert.assertThrows(RuntimeException.class, () -> stockClient.modifyStock("no-stock", "no-company", 0, 0));
    }

    @Test
    public void testQueryPrice() {
        Assert.assertEquals(200.0, stockClient.queryPrice("s1:google"), 5.0);
        Assert.assertEquals(20.0, stockClient.queryPrice("s2:yandex"), 5.0);
        Assert.assertEquals(1500.0, stockClient.queryPrice("s3:intel"), 5.0);
        Assert.assertThrows(IllegalArgumentException.class, () -> stockClient.queryPrice("s4:google"));
    }

    @Test
    public void testModify() {
        stockClient.modifyStock("s1", "google", -2, 100.0);
        Assert.assertEquals(300.0, stockClient.queryPrice("s1:google"), 5.0);
    }

    @Test
    public void testModifyTooLowQuantity() {
        Assert.assertThrows(RuntimeException.class, () -> stockClient.modifyStock("s3", "yandex", -1000, 0.0));
    }

    @Test
    public void testModifyTooLowPrice() {
        Assert.assertThrows(RuntimeException.class, () -> stockClient.modifyStock("s3", "yandex", 0, -1000.0));
    }
}
