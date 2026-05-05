public interface IPaymentProcessor {
    PaymentBreakdown preview(double subtotal, int pointsToRedeem, LoyaltyProgram lp);
    PaymentReceipt pay(Order order, Student student, int pointsToRedeem, double cashOrWallet,
                       LoyaltyProgram lp, PaymentStrategy method);
}
