public class PaymentBreakdown {
    public final double subtotal;
    public final double discountApplied;
    public final double totalDue;


    /** Three-arg variant if you already computed totalDue. */
    public PaymentBreakdown(double subtotal, double discountApplied, double totalDue) {
        //lw el subtotla b  -ve yhotha b zero
        this.subtotal = Math.max(0.0, subtotal);
        this.discountApplied = Math.max(0.0, discountApplied);
        this.totalDue = Math.max(0.0, totalDue);
    }

    @Override
    public String toString() {
        return "PaymentBreakdown{" +
                "subtotal=" + subtotal +
                ", discountApplied=" + discountApplied +
                ", totalDue=" + totalDue +
                '}';
    }
}
