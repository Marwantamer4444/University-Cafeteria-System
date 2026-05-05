public class PaymentProcessor implements IPaymentProcessor {

    @Override
    public PaymentBreakdown preview(double subtotal, int pointsToRedeem, LoyaltyProgram lp) {
        double discount = lp.previewDiscount(pointsToRedeem);
        if (discount < 0) discount = 0.0;
        if (discount > subtotal) discount = subtotal;
        double totalDue = subtotal - discount;
        return new PaymentBreakdown(subtotal, discount, totalDue);
    }

    @Override
    public PaymentReceipt pay(
            Order order,
            Student student,
            int pointsToRedeem,
            double cashOrWallet,
            LoyaltyProgram lp,
            PaymentStrategy method
    ) {
        if (order == null || student == null) {
            throw new IllegalArgumentException("order/student required");
        }

        // Subtotal = sum of order lines (free coffee has price 0.00)
        double subtotal = order.getTotalBeforeDiscount();

        // Apply variable points redemption
        double discountDouble = lp.applyRedemption(student, pointsToRedeem);
        if (discountDouble < 0) discountDouble = 0.0;
        if (discountDouble > subtotal) discountDouble = subtotal;

        double dueDouble = subtotal - discountDouble;
        if (cashOrWallet + 1e-6 < dueDouble) {
            throw new IllegalArgumentException("Insufficient payment amount");
        }

        int awarded = lp.pointsEarnedFromAmount(dueDouble);
        student.addPoints(awarded);

        double change = cashOrWallet - dueDouble;
        if (change < 0) change = 0.0;

        return new PaymentReceipt(
                order.getId(),
                subtotal,
                pointsToRedeem,
                discountDouble,
                dueDouble,   // amount charged
                awarded,
                student.getLoyaltyPoints()
        );
    }
}
