public class WalletPayment implements PaymentStrategy {
    private double balance;

    public WalletPayment(double initial) {
        this.balance = initial;
    }

    @Override
    public String getName() {
        return "Wallet (Balance: %.2f)".formatted(balance);
    }

    @Override
    public boolean pay(double amount) {
        if (amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public double getBalance() { return balance; }

    public void addFunds(double amount) {
        if (amount > 0) balance += amount;
    }
}
