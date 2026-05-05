public class CashPayment implements PaymentStrategy {
    @Override public String getName() {
        return "Cash";
    }
    @Override public boolean pay(double amount) {
        return true;
    }
}
