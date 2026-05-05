public class PaymentReceipt {
    public final String orderId;
    public final double subtotal;
    public final int pointsRedeemed;
    public final double discountApplied;
    public final double amountPaid;

    public final int pointsAwarded;
    public final int remainingPoints;

    public PaymentReceipt(String orderId, double subtotal, int pointsRedeemed, double discountApplied,
                          double amountPaid, int pointsAwarded, int remainingPoints) {
        this.orderId = orderId;
        this.subtotal = subtotal;
        this.pointsRedeemed = pointsRedeemed;
        this.discountApplied = discountApplied;
        this.amountPaid = amountPaid;

        this.pointsAwarded = pointsAwarded;
        this.remainingPoints = remainingPoints;
    }

    @Override public String toString() {
        return "Receipt{" +
                "orderId='" + orderId + '\'' +
                ", subtotal=" + subtotal +
                ", redeemed=" + pointsRedeemed +
                ", discount=" + discountApplied +
                ", paid=" + amountPaid +
                ", points+" + pointsAwarded +
                ", Points balance=" + remainingPoints +
                '}';
    }
}
