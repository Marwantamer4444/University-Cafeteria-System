import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money implements Comparable<Money> {

    // Always use a non-null rounding mode
    private static final RoundingMode RM = RoundingMode.HALF_UP;


    private final BigDecimal amount; // EGP, scale = 2

    private static BigDecimal scaled(BigDecimal v) {
        // Defensive: null-safety + enforce scale=2 with RM
        BigDecimal x = Objects.requireNonNull(v, "amount");
        return x.setScale(2, RM);
    }

    public Money(BigDecimal v) {
        this.amount = scaled(v);
    }

    public static Money of(double v) {
        return new Money(BigDecimal.valueOf(v));
    }



    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }

    public Money multiply(int qty) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(qty)));
    }



    public double asDouble() {
        return amount.doubleValue();
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " EGP";
    }

    @Override
    public int compareTo(Money o) {
        return this.amount.compareTo(o.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Money m)) return false;
        return this.amount.compareTo(m.amount) == 0;
    }

    @Override
    public int hashCode() {
        return amount.hashCode();
    }
}
