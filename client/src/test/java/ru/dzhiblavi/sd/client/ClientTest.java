package ru.dzhiblavi.sd.client;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.dzhiblavi.sd.client.entity.Client;
import ru.dzhiblavi.sd.client.model.ClientModel;
import ru.dzhiblavi.sd.client.model.InMemoryClientModel;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public class ClientTest extends BaseTest {
    private static ClientModel clientModel;
    private static final Random random = new Random(239);

    @BeforeClass
    public static void prepareClientModel() {
        clientModel = new InMemoryClientModel(stockClient);
    }

    private static String newRandomName() {
        byte[] array = new byte[16];
        random.nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }

    private Client newClient(final String name, final double funds) {
        clientModel.addClient(new Client(name, funds));
        final Client client = clientModel.getClient(name);
        Assert.assertEquals(client.getName(), name);
        Assert.assertEquals(client.getFunds(), funds, 0.0);
        Assert.assertEquals(client.getClientStocks(), List.of());
        return client;
    }

    @Test
    public void testNewClient() {
        newClient(newRandomName(), 10.0);
    }

    @Test
    public void testNewClientAlreadyExists() {
        final String name = newRandomName();
        newClient(name, 10.0);
        Assert.assertThrows(IllegalArgumentException.class, () -> newClient(name, 200.0));
    }

    @Test
    public void testAddFunds() {
        final String name = newRandomName();
        newClient(name, 50.0);
        clientModel.addFunds(name, 200.0);
        Assert.assertEquals(250.0, clientModel.getClient(name).getFunds(), 0.0);
    }

    @Test
    public void testBuyOrSellStocks() {
        final String name = newRandomName();
        final Client client = newClient(name, 4000.0);
        clientModel.buyOrSell(name, "s1", "google", 10);
        Assert.assertEquals(1, client.getClientStocks().size());
        Assert.assertEquals(10, client.getClientStocks().get(0).getQuantity());
        Assert.assertEquals(2000.0, client.getFunds(), 5.0);
        Assert.assertEquals(4000.0, clientModel.totalValue(name), 5.0);
        clientModel.buyOrSell(name, "s1", "google", -3);
        Assert.assertEquals(1, client.getClientStocks().size());
        Assert.assertEquals(7, client.getClientStocks().get(0).getQuantity());
        Assert.assertEquals(2600.0, client.getFunds(), 5.0);
        Assert.assertEquals(4000.0, clientModel.totalValue(name), 5.0);
        clientModel.buyOrSell(name, "s1", "google", -7);
        Assert.assertEquals(0, client.getClientStocks().size());
        Assert.assertEquals(4000.0, client.getFunds(), 5.0);
        Assert.assertEquals(4000.0, clientModel.totalValue(name), 5.0);
    }

    @Test
    public void testBuyStocksNotEnoughStocksInMarket() {
        final String name = newRandomName();
        final Client client = newClient(name, 100.0);
        Assert.assertThrows(RuntimeException.class, () -> clientModel.buyOrSell(name, "s1", "google", 1000));
        Assert.assertEquals(100.0, client.getFunds(), 0.0);
    }

    @Test
    public void testBuyStocksNotEnoughStocksInClient() {
        final String name = newRandomName();
        final Client client = newClient(name, 100.0);
        Assert.assertThrows(RuntimeException.class, () -> clientModel.buyOrSell(name, "s1", "google", -1000));
        Assert.assertEquals(100.0, client.getFunds(), 0.0);
    }

    @Test
    public void testBuyStocksNotEnoughFunds() {
        final String name = newRandomName();
        final Client client = newClient(name, 0.0);
        Assert.assertThrows(RuntimeException.class, () -> clientModel.buyOrSell(name, "s1", "google", 1));
        Assert.assertEquals(0.0, client.getFunds(), 0.0);
    }
}
