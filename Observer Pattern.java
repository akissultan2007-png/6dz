import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

interface IObserver {
    String getName();
    CompletableFuture<Void> updateAsync(String stockSymbol, double price);
}

interface IStockExchange {
    void registerObserver(String stockSymbol, IObserver observer);
    void removeObserver(String stockSymbol, IObserver observer);
    CompletableFuture<Void> notifyObserversAsync(String stockSymbol, double price);
}

class StockExchange implements IStockExchange {
    private final Map<String, List<IObserver>> observers = new HashMap<>();

    @Override
    public void registerObserver(String stockSymbol, IObserver observer) {
        observers.computeIfAbsent(stockSymbol, k -> new ArrayList<>()).add(observer);
        System.out.println("[LOG]: " + observer.getName() + " registered for " + stockSymbol);
    }

    @Override
    public void removeObserver(String stockSymbol, IObserver observer) {
        if (observers.containsKey(stockSymbol)) {
            observers.get(stockSymbol).remove(observer);
            System.out.println("[LOG]: " + observer.getName() + " removed from " + stockSymbol);
        }
    }

    public CompletableFuture<Void> updatePriceAsync(String stockSymbol, double newPrice) {
        System.out.printf("\n[MARKET]: %s -> %.2f\n", stockSymbol, newPrice);
        return notifyObserversAsync(stockSymbol, newPrice);
    }

    @Override
    public CompletableFuture<Void> notifyObserversAsync(String stockSymbol, double price) {
        if (observers.containsKey(stockSymbol)) {
            List<CompletableFuture<Void>> tasks = observers.get(stockSymbol).stream()
                    .map(obs -> obs.updateAsync(stockSymbol, price))
                    .collect(Collectors.toList());
            
            return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
        }
        return CompletableFuture.completedFuture(null);
    }
}

class Trader implements IObserver {
    private String name;

    public Trader(String name) { this.name = name; }

    @Override
    public String getName() { return name; }

    @Override
    public CompletableFuture<Void> updateAsync(String stockSymbol, double price) {
        System.out.printf("[Trader %s]: Received %s price: %.2f\n", name, stockSymbol, price);
        return CompletableFuture.completedFuture(null);
    }
}

class TradingRobot implements IObserver {
    private String name;
    private double threshold;
    private boolean buyAbove;

    public TradingRobot(String name, double threshold, boolean buyAbove) {
        this.name = name;
        this.threshold = threshold;
        this.buyAbove = buyAbove;
    }

    @Override
    public String getName() { return name; }

    @Override
    public CompletableFuture<Void> updateAsync(String stockSymbol, double price) {
        if (buyAbove && price >= threshold) {
            System.out.printf("[Robot %s]: ALERT! Price %.2f >= %.2f. ACTION: BUY %s\n", name, price, threshold, stockSymbol);
        } else if (!buyAbove && price <= threshold) {
            System.out.printf("[Robot %s]: ALERT! Price %.2f <= %.2f. ACTION: SELL %s\n", name, price, threshold, stockSymbol);
        }
        return CompletableFuture.completedFuture(null);
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        StockExchange exchange = new StockExchange();

        Trader john = new Trader("John");
        TradingRobot bot1 = new TradingRobot("Alpha-Bot", 150.0, true);
        TradingRobot bot2 = new TradingRobot("Omega-Bot", 100.0, false);

        exchange.registerObserver("AAPL", john);
        exchange.registerObserver("AAPL", bot1);
        exchange.registerObserver("TSLA", bot2);

        exchange.updatePriceAsync("AAPL", 140.0).get();
        Thread.sleep(500);

        exchange.updatePriceAsync("AAPL", 155.0).get();
        Thread.sleep(500);

        exchange.updatePriceAsync("TSLA", 90.0).get();
        Thread.sleep(500);

        exchange.removeObserver("AAPL", john);
        exchange.updatePriceAsync("AAPL", 160.0).get();

        System.out.println("\nSimulation completed.");
    }
}
