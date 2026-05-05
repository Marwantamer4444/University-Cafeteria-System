public interface PaymentStrategy {
    String getName();
    boolean pay(double amount);
}
