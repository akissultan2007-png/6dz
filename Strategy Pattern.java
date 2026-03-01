import java.util.Scanner;

interface IPaymentStrategy {
    void pay(double amount);
}


class CreditCardPayment implements IPaymentStrategy {
    private String cardNumber;

    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public void pay(double amount) {
        System.out.println("Төлем карта арқылы орындалды: " + amount + " тг. (Карта: " + cardNumber + ")");
    }
}

class PayPalPayment implements IPaymentStrategy {
    private String email;

    public PayPalPayment(String email) {
        this.email = email;
    }

    @Override
    public void pay(double amount) {
        System.out.println("Төлем PayPal арқылы орындалды: " + amount + " тг. (Аккаунт: " + email + ")");
    }
}


class CryptoPayment implements IPaymentStrategy {
    private String walletAddress;

    public CryptoPayment(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    @Override
    public void pay(double amount) {
        System.out.println("Төлем Криптовалюта арқылы орындалды: " + amount + " тг. (Әмиян: " + walletAddress + ")");
    }
}


class PaymentContext {
    private IPaymentStrategy strategy;


    public void setPaymentStrategy(IPaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void executePayment(double amount) {
        if (strategy == null) {
            System.out.println("Қате: Төлем әдісі таңдалмаған!");
            return;
        }
        strategy.pay(amount);
    }
}


public class Main {
    public static void main(String[] args) {
        PaymentContext context = new PaymentContext();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Төлем әдісін таңдаңыз:");
        System.out.println("1 - Банк картасы");
        System.out.println("2 - PayPal");
        System.out.println("3 - Криптовалюта");

        int choice = scanner.nextInt();

        System.out.print("Төлем сомасын енгізіңіз: ");
        double amount = scanner.nextDouble();

        switch (choice) {
            case 1:
                context.setPaymentStrategy(new CreditCardPayment("4400-1122-3344-5566"));
                break;
            case 2:
                context.setPaymentStrategy(new PayPalPayment("user@example.kz"));
                break;
            case 3:
                context.setPaymentStrategy(new CryptoPayment("0x71C249642404471212345"));
                break;
            default:
                System.out.println("Қате таңдау!");
                return;
        }

        
        context.executePayment(amount);
        
        scanner.close();
    }
}
